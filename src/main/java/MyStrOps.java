import java.io.IOException;
import java.util.function.Function;

class MyStrOps {


    static String myRelSS(Function<String,String> sf, String s) throws IOException {
        return sf.apply( s);
    }

    static String getks(String str) {
        StringBuilder s = new StringBuilder();
        if(str.contains("ff"))
            return str.replace("00", "");
        for(int i = 0; i < str.length(); i = str.indexOf(':', i) +1)
            s.append(Integer.valueOf(str.substring(i, str.indexOf(':', i))) - 30);
        return s.toString();
    }

    static String getprof(String prof) {
        StringBuilder sym = new StringBuilder();

        for(int i = 0; i <  changeShpd.profil.size() ; i++) {
            if (changeShpd.profil.get(i).equals(prof))
                sym.insert(0,Integer.toString(i) + "|");
            sym.append(changeShpd.profil.get(i)).append('|');
        }
        if(sym.charAt(0) == '*') {
            sym.insert(0,Integer.toString(changeShpd.profil.size() + 1) + "|");
            changeShpd.profil.add(prof);
            sym.append(prof).append('|');
        }

        return String.valueOf(sym);
    }
}
