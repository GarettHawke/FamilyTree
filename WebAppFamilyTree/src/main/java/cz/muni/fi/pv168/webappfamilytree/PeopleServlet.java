package cz.muni.fi.pv168.webappfamilytree;

import cz.muni.fi.pv168.familytree.*;
 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

@WebServlet(PeopleServlet.URL_MAPPING + "/*")
public class PeopleServlet extends HttpServlet {
 
    private static final String LIST_JSP = "/list1.jsp";
    private static final String UPDATE_JSP = "/update1.jsp";
    public static final String URL_MAPPING = "/people";
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            showPeopleList(request, response);
        }
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                String name = request.getParameter("name");
                String gender = request.getParameter("gender");
                String birthDate = request.getParameter("birthdate");
                String birthPlace = request.getParameter("birthplace");
                String deathDate = request.getParameter("deathdate");
                String deathPlace = request.getParameter("deathplace");
                if (name == null || name.length() == 0 ||
                        gender == null || gender.length() == 0 ||
                        birthDate == null || birthDate.length() == 0 ||
                        birthPlace == null || birthPlace.length() == 0) {
                    request.setAttribute("chyba", "Je nutné vyplnit hodnoty meno, pohlavie, dátum a miesto narodenia! Dátum a miesto úmrtia nepovinné.");
                    showPeopleList(request, response);
                    return;
                }
                try {
                    Person person = new Person(name, GenderType.valueOf(gender), birthPlace, LocalDate.parse(birthDate), deathPlace, (deathDate != null && deathDate.length() != 0) ? LocalDate.parse(deathDate) : null);
                    getPeopleManager().createPerson(person);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (IllegalArgumentException e) {
                    request.setAttribute("error", e.getMessage());
                    showPeopleList(request, response);
                } catch (ServiceFailureException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    getPeopleManager().deletePerson(getPeopleManager().findPersonById(id));
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/getUpdate":
                showPersonUpdate(request, response);
                return;
            case "/update":
                String nameU = request.getParameter("name");
                String genderU = request.getParameter("gender");
                String birthDateU = request.getParameter("birthdate");
                String birthPlaceU = request.getParameter("birthplace");
                String deathDateU = request.getParameter("deathdate");
                String deathPlaceU = request.getParameter("deathplace");
                if (nameU == null || nameU.length() == 0 ||
                        genderU == null || genderU.length() == 0 ||
                        birthDateU == null || birthDateU.length() == 0 ||
                        birthPlaceU == null || birthPlaceU.length() == 0) {
                    request.setAttribute("chyba", "Je nutné vyplnit hodnoty meno, pohlavie, dátum a miesto narodenia! Dátum a miesto úmrtia nepovinné.");
                    showPersonUpdate(request, response);
                    return;
                }
                try {
                    Person person = new Person(nameU, GenderType.valueOf(genderU), birthPlaceU, LocalDate.parse(birthDateU), deathPlaceU, (deathDateU != null && deathDateU.length() != 0) ? LocalDate.parse(deathDateU) : null);
                    person.setId(Long.valueOf(request.getParameter("id")));
                    getPeopleManager().updatePerson(person);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (IllegalArgumentException e) {
                    request.setAttribute("error", e.getMessage());
                    showPersonUpdate(request, response);
                } catch (ServiceFailureException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }
 
    private PeopleManager getPeopleManager() {
        return (PeopleManager) getServletContext().getAttribute("peopleManager");
    }
 
    private void showPeopleList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("people", getPeopleManager().findAllPeople());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (ServiceFailureException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void showPersonUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("person", getPeopleManager().findPersonById(Long.valueOf(request.getParameter("id"))));
            request.getRequestDispatcher(UPDATE_JSP).forward(request, response);
        } catch (ServiceFailureException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
 
}