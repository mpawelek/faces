package com.github.mpawelek.faces;

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;

import javax.faces.component.FacesComponent;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.util.*;

/**
 * Created by michal on 2017-04-07.
 */
@FacesComponent(value="com.github.mpawelek.faces.HtmlCommand")
public abstract class HtmlCommand extends UICommand implements ClientBehaviorHolder {

    private static final Attribute[] ATTRIBUTES = AttributeManager.getAttributes(AttributeManager.Key.COMMANDLINK);

    @Override
    public String getFamily() {
        return "custom";
    }

    public abstract String getTagName();

    @Override
    public void decode(FacesContext context) {
        Map<String, String> reqParams = context.getExternalContext().getRequestParameterMap();
        if (reqParams.containsKey(getClientId(context))) {
            queueEvent(new ActionEvent(this));
            return;
        }
        Map<String, List<ClientBehavior>> behaviors = getClientBehaviors();
        if (behaviors.isEmpty()) {
            return;
        }

//        for (String key : behaviors.keySet()) {
//            System.out.println("decode: " + key + ": " + behaviors.get(key));
//        }

        Map<String, String> req = context.getExternalContext().getRequestParameterMap();
        String event = req.get("javax.faces.behavior.event");

        //System.out.println("event: "+event);

        if (event == null) {
            return;
        }
        String source = req.get("javax.faces.source");

        //System.out.println("source: "+source);

        if (source == null) {
            return;
        }
        String clientId = getClientId(context);
        if (!source.equals(clientId)) {
            return;
        }
        for (ClientBehavior b : behaviors.get(event)) {
            b.decode(context, this);
        }
        queueEvent(new ActionEvent(this));
    }

    @Override
    public void encodeChildren(FacesContext context)
            throws IOException {

        if (!isRendered()) {
            return;
        }

        if (getChildCount() > 0) {
            for (UIComponent kid : getChildren()) {
                if (kid instanceof UIParameter) {
                    continue;
                }
                encodeRecursive(context, kid);
            }
        }
    }

    @Override
    public boolean getRendersChildren() {

        return true;
    }

    public void encodeRecursive(FacesContext context, UIComponent component) throws IOException {

        if (!component.isRendered()) {
            return;
        }

        component.encodeBegin(context);
        if (component.getRendersChildren()) {
            component.encodeChildren(context);
        } else {
            Iterator<UIComponent> kids = (Iterator<UIComponent>) component.getChildren();
            while (kids.hasNext()) {
                UIComponent kid = kids.next();
                encodeRecursive(context, kid);
            }
        }
        component.encodeEnd(context);
    }


    @Override
    public void encodeBegin(FacesContext context) throws IOException {

//        RenderKitUtils.renderJsfJsIfNecessary(context);
        context.getResponseWriter().startElement(getTagName(), this);

        ResponseWriter reply = context.getResponseWriter();
        reply.writeAttribute("id", getClientId(context), "id");
        reply.writeAttribute("name", getClientId(context), "clientId");
        String styleClass=(String)getAttributes().get("styleClass");
        if (styleClass != null) {
            reply.writeAttribute("class", styleClass, "styleClass");
        }

        RenderKitUtils.renderPassThruAttributes(
                context,
                reply,
                this,
                ATTRIBUTES,
                getClientBehaviors()
        );
        Collection<ClientBehaviorContext.Parameter> params = getBehaviorParameters(this);
        RenderKitUtils.renderOnclick(
                context, this, params, null, true);
        reply.flush();
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        ResponseWriter reply=context.getResponseWriter();
        reply.endElement(getTagName());
    }

    String getButtonType() {
        String type = (String) getAttributes().get("type");
        if (type == null || (!type.equals("submit") && !type.equals("button") && !type.equals("reset"))) {
            type = "submit";
            getAttributes().put("type", type);
        }
        return type;
    }

    List<ClientBehaviorContext.Parameter> getBehaviorParameters(UIComponent component) {
        List<ClientBehaviorContext.Parameter> params = null;
        if (getChildCount() < 1) {
            return params;
        }
        for (UIComponent kid : component.getChildren()) {
            if (kid instanceof UIParameter) {
                UIParameter p = (UIParameter) kid;
                String name = p.getName();
                Object value = p.getValue();
                if (!name.isEmpty()) {
                    if (params == null) {
                        params = new ArrayList<ClientBehaviorContext.Parameter>();
                    }
                    params.add(new ClientBehaviorContext.Parameter(name, value));
                }
            }
        }
        if (params == null) {
            return Collections.<ClientBehaviorContext.Parameter>emptyList();
        }
        return params;
    }

    @Override
    public Collection<String> getEventNames() {
        return Arrays.asList("click", "action", "mouseover", "mouseout");
    }

    @Override
    public String getDefaultEventName() {
        return "action";
    }
}

