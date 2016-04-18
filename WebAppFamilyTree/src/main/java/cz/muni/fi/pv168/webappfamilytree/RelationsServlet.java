package cz.muni.fi.pv168.webappfamilytree;

import cz.muni.fi.pv168.familytree.*;
 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(RelationsServlet.URL_MAPPING + "/*")
public class RelationsServlet extends HttpServlet {

    private static final String LIST_JSP = "/list3.jsp";
    public static final String URL_MAPPING = "/relations";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showRelationsList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                String par_id = request.getParameter("parent");
                String ch_id = request.getParameter("child");
                if (par_id == null || ch_id == null) {
                    request.setAttribute("chyba", "Je nutné vyplnit hodnoty rodič a dieťa!");
                    showRelationsList(request, response);
                    return;
                }
                try {
                    Person parent = getPeopleManager().findPersonById(Long.valueOf(par_id));
                    Person child = getPeopleManager().findPersonById(Long.valueOf(ch_id));
                    getRelationCatalog().makeRelation(parent, child);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long parent_id = Long.valueOf(request.getParameter("parent_id"));
                    Long child_id = Long.valueOf(request.getParameter("child_id"));
                    getRelationCatalog().deleteRelation(getPeopleManager().findPersonById(parent_id), getPeopleManager().findPersonById(child_id));
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/update":
                return;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }
    
    private PeopleManager getPeopleManager() {
        return (PeopleManager) getServletContext().getAttribute("peopleManager");
    }

    private RelationCatalog getRelationCatalog() {
        return (RelationCatalog) getServletContext().getAttribute("relationCatalog");
    }

    private void showRelationsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("relations", getRelationCatalog().findAllRelation());
            request.setAttribute("people", getPeopleManager().findAllPeople());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (ServiceFailureException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
 
}