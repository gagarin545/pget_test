import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;


import static java.lang.String.format;

class get_incident {
    private static Connection con;
    private static Statement stmt;

    static void start_bd() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:" + config.PORT  + "/zlt","ura", "Kukish54");
            con.setAutoCommit(false);
            stmt = con.createStatement();
            System.out.println("-- Opened database successfully");

        }catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }

    static void select(ClientThread client, Array codSity ) throws SQLException {
        int count = 0;

        client.sendmessage("&&" + Calendar.getInstance().getTime());

        System.out.println(format("select select_record( %s, array%s);", client.getKod_tit(), codSity).replace('{','[').replace('}',']'));

        ResultSet rs = stmt.executeQuery(format("select select_record( %s, array%s);", client.getKod_tit(), codSity).replace('{','[').replace('}',']'));

        while (rs.next()) {
            client.sendmessage(rs.getString("select_record"));
            count++;
        }

        client.sendmessage("&-");
        con.commit();
        System.out.println(client.getT().getName() + "->" + client.getMyName() + " послано " + count);
    }

    static ArrayList<String> get_td(String n_inc ) throws SQLException {
        ArrayList<String> td = new ArrayList<>();
        ResultSet rs = stmt.executeQuery(format("select service, kod_city, ip_address::inet, slot::text, port::text, ont::text, l_incident from incident where n_incident = %s; ", n_inc));

            while (rs.next()) {
                td.add( rs.getString("service"));
                td.add( rs.getString("kod_city"));
                td.add( rs.getString("ip_address"));
                td.add( rs.getString("slot"));
                td.add( rs.getString("port"));
                td.add( rs.getString("ont"));
                td.add( rs.getString("l_incident"));

            }
            con.commit();
            return td;
    }

    static void get_work_incident(ClientThread client, String n_incident) throws SQLException {
        System.out.println(format("UPDATE incident set worker = work.id FROM (select w.id from workers w where imei = '%s') AS work WHERE n_incident = %s;",client.getT().getName(), n_incident));
        stmt.executeUpdate(format("UPDATE incident set worker = work.id FROM (select w.id from workers w where imei = '%s') AS work WHERE n_incident = %s;",client.getT().getName(), n_incident));
        con.commit();
    }


    static Array get_worker(ClientThread o) throws SQLException {

        ResultSet rs = stmt.executeQuery( "select name, kodcity from workers where imei = '" + o.getT().getName() + "';");
        System.out.println("Name-" +o.getT().getName());
        if(rs.next()) {
            o.putMyName( rs.getString("name"));
            return rs.getArray("kodcity");
        }
        else
            return null;
    }

    static void registration(String name, ClientThread client) throws SQLException { // сообщение , клиент
        //stmt.execute(format ("select worker_record(%s,'%s', '%s');" ,client.getKod_tit() , client.getT().getName(), name.substring(0,name.indexOf('-')) ));
        System.out.println(format ("select worker_registration('%s','%s', '%s');" , client.getT().getName(), name.substring(0,name.indexOf('-')), name.substring(name.indexOf('-') + 1) ));
        stmt.execute(format ("select worker_registration('%s','%s', '%s');" , client.getT().getName(), name.substring(0,name.indexOf('-')), name.substring(name.indexOf('-') + 1).trim() ));
        con.commit();
    }


}
