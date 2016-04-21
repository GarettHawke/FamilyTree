package cz.muni.fi.pv168.webappfamilytree;

import cz.muni.fi.pv168.familytree.Main;
import cz.muni.fi.pv168.familytree.PeopleManagerImpl;
import cz.muni.fi.pv168.familytree.MarriageCatalogImpl;
import cz.muni.fi.pv168.familytree.RelationCatalogImpl;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

public class StartListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ServletContext servletContext = sce.getServletContext();
            DataSource dataSource = Main.createMemoryDatabase();
            PeopleManagerImpl peopleManager = new PeopleManagerImpl(dataSource);
            servletContext.setAttribute("peopleManager", peopleManager);
            MarriageCatalogImpl marriageCatalog = new MarriageCatalogImpl(dataSource);
            marriageCatalog.setPeopleManager(peopleManager);
            servletContext.setAttribute("marriageCatalog", marriageCatalog);
            RelationCatalogImpl relationCatalog = new RelationCatalogImpl(dataSource);
            relationCatalog.setPeopleManager(peopleManager);
            servletContext.setAttribute("relationCatalog", relationCatalog);
        } catch (SQLException | IOException ex) {
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
