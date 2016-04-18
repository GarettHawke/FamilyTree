package cz.muni.fi.pv168.webappfamilytree;

import cz.muni.fi.pv168.familytree.*;
 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet(MarriagesServlet.URL_MAPPING + "/*")
public class MarriagesServlet extends HttpServlet {

    private static final String LIST_JSP = "/list2.jsp";
    public static final String URL_MAPPING = "/marriages";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showMarriagesList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                String spouse1_id = request.getParameter("spouse1");
                String spouse2_id = request.getParameter("spouse2");
                String from = request.getParameter("from");
                String to = request.getParameter("to");
                if (spouse1_id == null || spouse2_id == null ||
                        from == null || from.length() == 0) {
                    request.setAttribute("chyba", "Je nutné vyplnit partnerov a dátum uzavretia manželstva! Dátum skončenia manželstva je nepovinný.");
                    showMarriagesList(request, response);
                    return;
                }
                try {
                    Marriage marriage = new Marriage(
                            getPeopleManager().findPersonById(Long.valueOf(spouse1_id)),
                            getPeopleManager().findPersonById(Long.valueOf(spouse2_id)),
                            LocalDate.parse(from),
                            (to != null && to.length() != 0) ? LocalDate.parse(to) : null);
                    getMarriageCatalog().createMarriage(marriage);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    getMarriageCatalog().deleteMarriage(getMarriageCatalog().findMarriageById(id));
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

    private MarriageCatalog getMarriageCatalog() {
        return (MarriageCatalog) getServletContext().getAttribute("marriageCatalog");
    }

    private void showMarriagesList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("marriages", getMarriageCatalog().findAllMarriages());
            request.setAttribute("people", getPeopleManager().findAllPeople());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (ServiceFailureException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
 
}