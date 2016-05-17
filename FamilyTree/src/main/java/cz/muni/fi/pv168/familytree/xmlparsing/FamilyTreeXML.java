package cz.muni.fi.pv168.familytree.xmlparsing;

import cz.muni.fi.pv168.familytree.GenderType;
import cz.muni.fi.pv168.familytree.Marriage;
import cz.muni.fi.pv168.familytree.Pair;
import cz.muni.fi.pv168.familytree.Person;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 *
 * @author Peter
 */
public class FamilyTreeXML {
    private List<Person> people;
    private List<Marriage> marriages;
    private Map<Person, List<Person>> relations;
    
    private final File file;
    
    public FamilyTreeXML(File file) {
        this.file = file;
    }
    
    public boolean parse() {
        people = new ArrayList<>();
        marriages = new ArrayList<>();
        relations = new HashMap<>();
        
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            
            NodeList list = doc.getElementsByTagName("Person");
            
            for (int i = 0; i < list.getLength(); i++) {
                Node item = list.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE)
                    people.add(createPerson((Element)item));
            }
            
            list = doc.getElementsByTagName("Marriage");
            
            for (int i = 0; i < list.getLength(); i++) {
                Node item = list.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE)
                    marriages.add(createMarriage((Element)item));
            }
            
            list = doc.getElementsByTagName("Relation");
            
            for (int i = 0; i < list.getLength(); i++) {
                Node item = list.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Pair<Person, Person> p = createRelation((Element)item);
                    if (!relations.containsKey(p.getL()))
                        relations.put(p.getL(), new ArrayList<>());
                    relations.getOrDefault(p.getL(), null).add(p.getR());
                }
            }
            
            return true;
        } catch(ParserConfigurationException | SAXException | IOException ex) {
            return false;
        }
    }
    
    public boolean create(List<Person> peopleList, List<Marriage> marriagesList, Map<Person, List<Person>> relationsList) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            Element rootElement = doc.createElement("FamilyTree");
            doc.appendChild(rootElement);
            
            Element peopleElement = doc.createElement("People");
            rootElement.appendChild(peopleElement);
            for (Person person : peopleList) {
                peopleElement.appendChild(createPerson(person, doc));
            }
            
            Element marriagesElement = doc.createElement("Marriages");
            rootElement.appendChild(marriagesElement);
            for (Marriage marriage : marriagesList) {
                marriagesElement.appendChild(createMarriage(marriage, doc));
            }
            
            Element relationsElement = doc.createElement("Relations");
            rootElement.appendChild(relationsElement);
            for (Map.Entry<Person, List<Person>> parent : relationsList.entrySet()) {
                for (Person child : parent.getValue()) {
                    relationsElement.appendChild(createRelation(parent.getKey(), child, doc));
                }
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
            
            return true;
        } catch(ParserConfigurationException | DOMException | TransformerException ex) {
            return false;
        }
    }
    
    private Element createPerson(Person person, Document doc) {
        Element p = doc.createElement("Person");
        p.setAttribute("id", person.getId().toString());
        p.setAttribute("name", person.getName());
        p.setAttribute("gender", person.getGender().toString());
        p.setAttribute("birthDate", person.getDateOfBirth().toString());
        p.setAttribute("birthPlace", person.getPlaceOfBirth());
        if (person.getDateOfDeath() != null) {
            p.setAttribute("deathDate", person.getDateOfDeath().toString());
            p.setAttribute("deathPlace", person.getPlaceOfDeath());
        }
        return p;
    }

    private Element createMarriage(Marriage marriage, Document doc) {
        Element m = doc.createElement("Marriage");
        //m.setAttribute("id", marriage.getId().toString());
        m.setAttribute("sp1_id", marriage.getSpouse1().getId().toString());
        m.setAttribute("sp2_id", marriage.getSpouse2().getId().toString());
        m.setAttribute("from", marriage.getFrom().toString());
        if (marriage.getTo() != null)
            m.setAttribute("to", marriage.getTo().toString());
        return m;
    }

    private Element createRelation(Person parent, Person child, Document doc) {
        Element r = doc.createElement("Relation");
        r.setAttribute("parent_id", parent.getId().toString());
        r.setAttribute("child_id", child.getId().toString());
        return r;
    }

    public List<Person> getPeople() {
        return people;
    }

    public List<Marriage> getMarriages() {
        return marriages;
    }

    public Map<Person, List<Person>> getRelations() {
        return relations;
    }

    private Person createPerson(Element element) {
        Person p = new Person();
        p.setId(Long.parseLong(element.getAttribute("id")));
        p.setName(element.getAttribute("name"));
        p.setGender(GenderType.valueOf(element.getAttribute("gender")));
        p.setDateOfBirth(LocalDate.parse(element.getAttribute("birthDate")));
        p.setPlaceOfBirth(element.getAttribute("birthPlace"));
        if (element.hasAttribute("deathDate")) {
            p.setDateOfDeath(LocalDate.parse(element.getAttribute("deathDate")));
            p.setPlaceOfDeath(element.getAttribute("deathPlace"));
        }
        return p;
    }

    private Marriage createMarriage(Element element) {
        Marriage m = new Marriage();
        Person p = new Person();
        p.setId(Long.parseLong(element.getAttribute("sp1_id")));
        m.setSpouse1(people.get(people.indexOf(p))); //works because person = person if their ids equal
        p.setId(Long.parseLong(element.getAttribute("sp2_id")));
        m.setSpouse2(people.get(people.indexOf(p))); //works because person = person if their ids equal
        m.setFrom(LocalDate.parse(element.getAttribute("from")));
        if (element.hasAttribute("to"))
            m.setTo(LocalDate.parse(element.getAttribute("to")));
        return m;
    }

    private Pair<Person, Person> createRelation(Element element) {
        Person p = new Person();
        p.setId(Long.parseLong(element.getAttribute("parent_id")));
        Person parent = people.get(people.indexOf(p)); //works because person = person if their ids equal
        p.setId(Long.parseLong(element.getAttribute("child_id")));
        Person child = people.get(people.indexOf(p)); //works because person = person if their ids equal
        return new Pair<>(parent, child);
    }
}
