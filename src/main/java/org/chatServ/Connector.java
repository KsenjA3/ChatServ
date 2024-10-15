package org.chatServ;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

class Connector {
    SessionFactory sessionFactory;

    public Connector(String path) {
//        sessionFactory= new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

        sessionFactory= new Configuration()
                .configure(path)
                .addAnnotatedClass(Users.class)
                .addAnnotatedClass(Messages.class)
                .addAnnotatedClass(MessagesUsers.class)
                .addAnnotatedClass(MessagesUsersID.class)
                .buildSessionFactory();
    }



    public Session getSession() {
//        return sessionFactory.getCurrentSession();
        return sessionFactory.openSession();
    }
}
