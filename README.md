A JSF component 'div' with onclick, onmouseover, onmouseout capabilities.

A simmple usage:

<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html>
<html lang="en"
      xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:my="com.github.mpawelek.faces"
>
<h:head>
    <h:outputScript library="javax.faces" name="jsf.js"/>
</h:head>
<h:body>
    <h:form>
        <my:div>
            <f:ajax event="click" listener="#{test.action}"/>
            <f:ajax event="mouseover" listener="#{test.over}"/>
            <f:ajax event="mouseout" listener="#{test.out}"/>
            ala ma kota
        </my:div>
    </h:form>
</h:body>
</html>
