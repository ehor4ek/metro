import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class NextSt {
    public String name = "";
    public String line = "";
    NextSt(String data) {
        if (data.indexOf("_") == -1)
            this.name = data;
        else {
            String[] dataItems = data.split("_");
            this.line = dataItems[0];
            this.name = dataItems[1];
        }
    }

    @Override
    public String toString() {
        return line.toString() + name.toString();
    }
}

class Station {
    Map<Integer, String> lines = new HashMap<Integer, String>();
    Map<String, Integer> lines2 = new HashMap<String, Integer>();
    public String line = "";
    public String name = "";
    public int lineNumber = 0;
    public int stationNumber = 0;
    public boolean visited = false;
    public ArrayList<Station> nextStations = new ArrayList<Station>();
    public ArrayList<NextSt> nextNames = new ArrayList<NextSt>();

    Station(String line, String name, Station lStation, int stationNumber) {
        lines.put(1, "Сокольническая линия");
        lines.put(2, "Замоскворецкая линия");
        lines.put(3, "Арбатско-Покровская линия");
        lines.put(4, "Филевская линия");
        lines.put(5, "Кольцевая линия");
        lines.put(6, "Калужско-Рижская линия");
        lines.put(7, "Таганско-Краснопресненская линия");
        lines.put(8, "Калининско-Солнцевская линия");
        lines.put(9, "Серпуховско-Тимирязевская линия");
        lines.put(10, "Люблинско-Дмитровская линия");
        lines.put(11, "Каховская линия");
        lines.put(12, "Бутовская линия");
        lines.put(13, "Московский монорельс");
        lines.put(14, "МЦК");
        lines.put(15, "Большая Кольцевая линия");
        lines.put(16, "Некрасовская линия");
        lines.put(17, "МЦД-1");
        lines.put(18, "МЦД-2");
        lines.put(19, "МЦД-3");
        lines.put(20, "МЦД-4");
        lines.put(21, "МЦД-4A");
        for (Integer key : lines.keySet()) {
            lines2.put(lines.get(key), key);
        }
        this.name = name;
        this.line = line;
        this.stationNumber = stationNumber;
        System.out.println(line);
        this.lineNumber = lines2.get(line);
        System.out.println(line + lineNumber);
        if (lStation != null && lStation != this) {
            nextStations.add(lStation);
            lStation.nextStations.add(this);
            // lStation.nextNames.add(new NextSt(line, name));
            // nextNames.add(new NextSt(lStation.line, lStation.name));

            // System.out.println(this.name + this.next + "Last" + lStation.name + lStation.next);
        }
    }

    // Station(String line, String name, String lStation) {
    //     this.name = name;
    //     this.line = line;
    //     this.next = lStation;
    // }

    public void addNext(String next) {
        // nextStations.add(new Station("", next, this));
        if (next.indexOf(";") == -1)
            this.nextNames.add(new NextSt(next));
        else {
            String[] nexts = next.split(";");
            for (int i = 0; i < nexts.length; i++) {
                this.nextNames.add(new NextSt(nexts[i]));
            }
        }
    }

    public void addStation(Station S) {
        nextStations.add(S);
    }

    public boolean contains(Station st) {
        boolean l = false;
        NextSt checkingSt;
        for (int i = 0; i < nextNames.size(); i++) {
            checkingSt = nextNames.get(i);
            if ((checkingSt.line.equals("") || checkingSt.line.equals(st.line)) && checkingSt.name.equals(st.name)) {
                l = true;
                break;
            }
        }
        return l;
    }

    @Override
    public String toString() {
        String s = name;
        if (!nextStations.isEmpty()) {
            s += "_" + nextStations.get(0).line.toString() + ":" + nextStations.get(0).name.toString();
            for (int i = 1; i < nextStations.size(); i++) {
                s += ";" + nextStations.get(i).line.toString() + ":" + nextStations.get(i).name.toString();
            }
        }
        return s; 
    }
}

class baseForData {
    public static boolean notException(String s) {
        if (s.equals("Тестовская") || s.equals("Смоленская") || s.equals("Люблино") || s.equals("Солнечная") || s.equals("Арбатская")) {
                return false;
        }
        return true;
    }

    public static void main(String[] args) {
        ArrayList<Station> allStations = new ArrayList<Station>();
        String line = "";
        Station lastStation = null;
        String[] data;
        try(FileReader f = new FileReader(new File("stationsWithLinks.txt"))) {
            BufferedReader reader = new BufferedReader(f);
            String buffer = "";
            FileWriter writer = new FileWriter("output.txt");
            while((buffer = reader.readLine()) != null) {
                if (buffer.equals("") || buffer.equals("\n") || buffer.equals(" ")) {
                    // System.out.println("Empty");
                    line = "";
                    lastStation = null;
                } else {
                    if (line.equals("")) {
                        // System.out.println("Line");
                        line = buffer;
                        System.out.println(line);
                        lastStation = null;
                    } else {
                        if (buffer.indexOf("_") != -1) {
                            // System.out.println("Station with next");
                            data = buffer.split("_");
                            lastStation = new Station(line, data[0], lastStation, allStations.size() + 1);
                            allStations.add(lastStation);
                            lastStation.addNext(data[1]);
                        } else {
                            // System.out.println("Just station");
                            lastStation = new Station(line, buffer, lastStation, allStations.size() + 1);
                            allStations.add(lastStation);
                        }
                        // writer.write(lastStation.toString() + "\n");
                        // writer.write(allStations.get(allStations.size() - 1).toString() + "\n");
                        // writer2.write(allStations.size() + ":{'line': " + lastStation.line + ",'name': '" + lastStation.name + "'},\n");
                    }
                }
            }
            Station exactStation, checkingStation;
            for (int i = 0; i < allStations.size(); i++) {
                exactStation = allStations.get(i);
                for (int j = 0; j < allStations.size(); j++) {
                    if (j != i) {
                        checkingStation = allStations.get(j);
                        if (notException(exactStation.name) && notException(checkingStation.name) && exactStation.name.equals(checkingStation.name)) {
                            if(!exactStation.nextStations.contains(checkingStation)) 
                                exactStation.addStation(checkingStation);
                            if(!checkingStation.nextStations.contains(exactStation))
                                checkingStation.addStation(exactStation);
                        } else {
                            if (exactStation.contains(checkingStation) || checkingStation.contains(exactStation)) {
                                if(!exactStation.nextStations.contains(checkingStation)) 
                                    exactStation.addStation(checkingStation);
                                if(!checkingStation.nextStations.contains(exactStation))
                                    checkingStation.addStation(exactStation);                          
                            }
                        }
                    }
                }
            }

            FileWriter writer2 = new FileWriter("stationsJson.txt");
            FileWriter writer3 = new FileWriter("links.txt");
            Station st;
            for (int i = 0; i < allStations.size(); i++) {
                st = allStations.get(i);
                writer2.write(" " + (i+1) + ": {'line': " + st.lineNumber + ", 'name': '" + st.name + "'},\n");
                for (int j = 0; j < st.nextStations.size(); j++) {
                    writer3.write("(" + st.stationNumber + ", " + st.nextStations.get(j).stationNumber + ", " + (int)(Math.random() * 500 + 100) + "),\n");
                }
            }
            writer2.flush();
            writer2.write("abc\n");
            writer2.close();
            writer3.flush();
            writer3.write("abc\n");
            writer3.close();

            for (int i = 0; i < allStations.size(); i++) {
                writer.write(allStations.get(i).toString() + "\n");
            }
            writer.close();
        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}