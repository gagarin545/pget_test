public class binary {

    // System.out.println( Nport(String.valueOf(p), String.valueOf(i), "01"));

    static int Nport(String p, String sl, String l){
        System.out.println("|" + p + "|" + sl + "|");
        if(sl == null)  return 0;
        int i = Integer.parseInt(sl);
        if ( i < 32 )
            return Integer.parseInt(String.format("%4s%5s%2s%15s"
                    , Integer.toBinaryString(Integer.parseInt(p))
                    , Integer.toBinaryString(i)
                    , l
                    , ' ').replace(' ', '0') , 2);
        else
            if( i == 64 )
                return Integer.parseInt(String.format("%4s%5s%2s%8s%7s"
                        , Integer.toBinaryString(Integer.parseInt(p))
                        , Integer.toBinaryString(i - 64)
                        , l
                        ,'1'
                        ,' ').replace(' ', '0') , 2);
            else
                return Integer.parseInt(String.format("%4s%5s%2s%9s%6s"
                        , Integer.toBinaryString(Integer.parseInt(p))
                        , Integer.toBinaryString(i - 32)
                        , l
                        , '1'
                        , ' ').replace(' ', '0') , 2);
    }
}
