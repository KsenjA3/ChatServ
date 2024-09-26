package org.chatServ;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

class Connector {
    SessionFactory sessionFactory;

    public Connector() {
        sessionFactory= new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }



    public Session getSession() {
        return sessionFactory.openSession();
    }
}
