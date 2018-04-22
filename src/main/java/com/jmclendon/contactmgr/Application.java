package com.jmclendon.contactmgr;

import com.jmclendon.contactmgr.Model.Contact;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;


public class Application {

    // Hold a reusable reference to a SessionFactory (since we only need one)
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {

        //Create a StandardServiceRegistry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {

        Contact contact = new Contact.ContactBuilder("Josh", "Mclendon")
                .withEmail("basics@hibernate.com")
                .withPhone(1234567890L)
                .build();

        int id = save(contact);

        //Display a list of contacts before the update
        System.out.printf("%n%nBefore update%n%n");
        fetchAllContacts().forEach(System.out::println);

        //Get the persisted contact
        Contact c = findContactById(id);

        //Update the contact
        c.setFirstName("Joshua");

        //Persist the changes
        System.out.printf("%n%nUpdating...%n%n");
        update(c);
        System.out.printf("%n%nAfter update%n%n");

        //Display a list of contacts after the update
        fetchAllContacts().forEach(System.out::println);

        System.out.printf("%n%nDeleting...%n%n");
        delete(c);
        System.out.printf("%n%nAfter deletion%n%n");

        //Display a list of contacts after the delete
        fetchAllContacts().forEach(System.out::println);
    }

    private static Contact findContactById(int id) {

        //Open a session
        Session session = sessionFactory.openSession();

        //Retrieve the persistent object (or null if not found)
        Contact contact = session.get(Contact.class, id);

        //Close the session
        session.close();

        //Return the object
        return contact;
    }

    private static void delete(Contact contact) {

        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to update the contact
        session.delete(contact);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }

    private static void update(Contact contact) {

        //Open a session
        Session session = sessionFactory.openSession();

        //Begin a transaction
        session.beginTransaction();

        //Use the session to update contact
        session.update(contact);

        //Commit the transaction
        session.getTransaction().commit();

        //Close the session
        session.close();
    }

    public static List<Contact> fetchAllContacts() {

        //Open a session
        Session session = sessionFactory.openSession();

        //Create CriteriaBuilder

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        //Create CriteriaQuery

        CriteriaQuery<Contact> criteria = criteriaBuilder.createQuery(Contact.class);

        //Specify criteria root

        criteria.from(Contact.class);

        //Execute query
        List<Contact> contacts = session.createQuery(criteria).getResultList();

        //Close the session
        session.close();

        return contacts;
    }

    public static int save(Contact contact) {

        //Open a session
        Session session = sessionFactory.openSession();

        //Begin a transaction
        session.beginTransaction();

        //Use the session to save the contact
        int id = (int) session.save(contact);

        //Commit the transaction
        session.getTransaction().commit();

        //Close session
        session.close();

        return id;
    }

}
