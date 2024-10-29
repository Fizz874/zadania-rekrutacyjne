/**
 * Z podanego zbioru danych wyselekcjonuj 5 o największej wartości na jednostkę, znając kategorię obiektu</br>
 * Dane znajdują się w folderze "dane" w pliku "zbiór_wejściowy.json" oraz "kategorie.json"
 * Wynik przedstaw w czytelnej formie na standardowym wyjściu
 *
 *
 */
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.util.Comparator;

public class Main {

    public static ArrayList<HashMap<String,String>> parse( List<String> file){
        //Prowizoryczny parser - działający tylko dla formatu danych w tym konkretnym problemie

        ArrayList<HashMap<String,String>> tab = new ArrayList<HashMap<String,String>>();

        HashMap<String,String> map = new HashMap<String,String>();;
        for(String l:file){
            l.trim();
            if (l.contains("{")){
                map = new HashMap<String,String>();
        
            } else if (l.contains("},") || l.contains("}")){
                tab.add(map);

            } else if(!l.contains("[") && !l.contains("]")) {
                if(l.endsWith(",")){
                    l = l.substring(0, l.length()-1);
                }

                String[] keyval = l.split("\": ");
                map.put(keyval[0].replaceAll(".*\s\"", ""),keyval[1]);
            }
        }
        return tab;
    }




    public static void main(String[] args) throws IOException {

        List<String> allData = Files.readAllLines(Paths.get("dane/zbiór_wejściowy.json"));
        List<String> allKat = Files.readAllLines(Paths.get("dane/kategorie.json"));

        ArrayList<HashMap<String,String>> dane = parse(allData);
        ArrayList<HashMap<String,String>> kategorie = parse(allKat);

        for(HashMap<String,String> v:dane){
            String typ = v.get("Typ");
            String czystosc = v.get("Czystość");

            int val = 0;
            for(HashMap<String,String> kat: kategorie){
                if(typ.equals(kat.get("Typ")) && czystosc.equals(kat.get("Czystość"))){
                    val = Integer.parseInt(kat.get("Wartość za uncję (USD)"));                    
                    break;
                }
            }


            String masa = v.get("Masa");
            float masaGram = 0;
            if(masa.contains("ct")){
                masa = masa.substring(1, masa.length()-3).replace(',', '.');
                masaGram = (Float.parseFloat(masa)*0.2f);

            } else
            {
                masa = masa.substring(1, masa.length()-2).replace(',', '.');
                masaGram = Float.parseFloat(masa);

            }

            float rez =  ((float)(val / 28.3495231) * masaGram); //Wartość w dolarach
            v.put("Wynik", Float.toString(rez));
            v.put("Wzu", Integer.toString(val));
        }

        Comparator<HashMap<String,String>> comparator = new HashMapComparator();
        dane.sort(comparator);
        System.out.println("Pięć obiektów o największej wartości na jednostkę: ");
        for(int i = 0; i < 5; i++){

            if (Float.parseFloat(dane.get(i).get("Wynik")) == 0.0f){
                System.out.println("Nie mamy wystarczająco informaji o kategoriach pozostałych obiektów");
                break;
            }

            System.out.println("\n" + Integer.toString(i+1) +". " + dane.get(i).get("Typ").replace("\"", "") + " o wartosci: " + 
            (float)Math.round( Float.parseFloat(dane.get(i).get("Wynik")) *100)/100 + " USD" );
            System.out.println("Masa: " + dane.get(i).get("Masa").replace("\"", "") + "; Czystość: " + dane.get(i).get("Czystość").replace("\"", "") 
            + "; Wartość za uncję (USD): " + dane.get(i).get("Wzu") );
            System.out.println("Barwa: " + dane.get(i).get("Barwa") + "; Pochodzenie: " + dane.get(i).get("Pochodzenie") + "; Właściciel: "+ dane.get(i).get("Właściciel"));
        }
    }

}


class HashMapComparator implements Comparator<HashMap<String,String>> {
    @Override
    public int compare(HashMap<String,String> x, HashMap<String,String> y) {
        if (Float.parseFloat(x.get("Wynik")) < Float.parseFloat(y.get("Wynik"))) {
            return 1;
        }
        if (Float.parseFloat(x.get("Wynik")) > Float.parseFloat(y.get("Wynik"))) {
            return -1;
        }
        return 0;
    }
}

