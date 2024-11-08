package gr.aueb.cf;

import gr.aueb.cf.firstapp.model.Teacher;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;

public class App
{
    public static void main( String[] args ) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("teachersPU");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Teacher teacher1 = new Teacher();
        teacher1.setFirstname("Athanassios");
        teacher1.setLastname("Androutsos");

        Teacher teacher2 = new Teacher();
        teacher2.setFirstname("Makis");
        teacher2.setLastname("Kapetis");

        Teacher teacher3 = new Teacher();
        teacher3.setFirstname("Anna");
        teacher3.setLastname("Giannoutsos");

        em.persist(teacher1);
        em.persist(teacher2);
        em.persist(teacher3);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Teacher newTeacher = new Teacher();
        newTeacher.setId(2L);
        newTeacher.setFirstname("Chrystostomos");
        newTeacher.setLastname("Kap.");
        em.merge(newTeacher);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Teacher athanassios = em.find(Teacher.class, 1L);
        em.getTransaction().commit();

        System.out.println(athanassios);

        em.getTransaction().begin();
        Teacher teacher = em.find(Teacher.class, 2L);
        em.remove(teacher);
        em.getTransaction().commit();

        em.getTransaction().begin();
        //TypedQuery<Teacher> teacherTypedQuery = em.createQuery("select t from Teacher t", Teacher.class);
        //List<Teacher> teachers = teacherTypedQuery.getResultList();
        List<Teacher> teachers = em.createQuery("select t from Teacher t", Teacher.class).getResultList();
        em.getTransaction().commit();

        teachers.forEach(System.out::println);


        em.getTransaction().begin();
        TypedQuery<Teacher> teachersNamedAthanassios = em.createQuery("select t from Teacher t where t.firstname = :name", Teacher.class);
        //TypedQuery<Teacher> teachersNamedAthanassios = em.createQuery("select t from Teacher t where t.firstname = ?1", Teacher.class);
        //teachersNamedAthanassios.setParameter(1, "Athanassios");
        teachersNamedAthanassios.setParameter("name", "Athanassios");
        Teacher athana = teachersNamedAthanassios.getSingleResult();
        em.getTransaction().commit();

        System.out.println(athana);

        em.getTransaction().begin();
        Query countQuery = em.createQuery("select count(*) from Teacher");
        Long result = (Long) countQuery.getSingleResult();
        em.getTransaction().commit();
        System.out.println("Teacher Entities Count: " + result);

        em.getTransaction().begin();

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Teacher> criteriaQuery = builder.createQuery(Teacher.class);
        Root<Teacher> root = criteriaQuery.from(Teacher.class);
        ParameterExpression<String> firstnameParam = builder.parameter(String.class, "teacherFirstname");
        //criteriaQuery.select(root).where(builder.equal(root.get("firstname"), "Athanassios"));
        criteriaQuery.select(root).where(builder.equal(root.get("firstname"), firstnameParam));
        //criteriaQuery.select(root).where(builder.like(root.get("firstname"), firstnameParam));

        // criteriaQuery.select(root); seems like "select t from Teacher t"
        // root.get() returns path

       /* criteriaQuery.select(root).where(builder.and(builder.equal(root.get("firstname"), "Athanassios")),
                                            builder.like(root.get("lastname"),"Androu%"));*/


        TypedQuery<Teacher> query = em.createQuery(criteriaQuery);
        //query.setParameter("teacherFirstname", "Athana%");
        query.setParameter("teacherFirstname", "Athanassios");
        List<Teacher> teacherList = query.getResultList();
        em.getTransaction().commit();

        System.out.println("GET FROM Criteria Query");
        teacherList.forEach(System.out::println);

        em.close();
        emf.close();
    }
}
