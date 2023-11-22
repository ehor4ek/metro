import java.io.*;
import java.util.ArrayList;

class NextSt { //класс для хранения пересадки
    public String name = ""; //поле имени станции
    public String line = ""; //поле линии станции
    NextSt(String data) { //конструктор создания пересадки
        if (data.indexOf("_") == -1) //если станция не содержит инфы о линии (нет разделителя _) 
            this.name = data;
        else {
            String[] dataItems = data.split("_"); //содержит инфу о линии (есть разделитель _) 
            this.line = dataItems[0];
            this.name = dataItems[1];
        }
    }

    @Override //вывод
    public String toString() {
        return line.toString() + name.toString();
    }
}

class Station { //объект станции
    public String line = ""; //поле линни
    public String name = ""; //поле названия станции
    public int stationNumber = 0; //поле индекса станции
    public boolean visited = false; //лог. переменная: посещена ли станции при обходе графа в ширину
    public ArrayList<Station> nextStations = new ArrayList<Station>(); //список объектов следующих станций
    public ArrayList<NextSt> nextNames = new ArrayList<NextSt>(); //список пересадок
    public static int links = 0; //переменная количества связей между станциями

    Station(String line, String name, Station lStation, int stationNumber) { //конструктор создания объекта станции
        this.name = name; 
        this.line = line;
        this.stationNumber = stationNumber;
        if (lStation != null && lStation != this) { //создание связи с предыдущей станцией в линии
            nextStations.add(lStation);
            lStation.nextStations.add(this); //добавление этой станции в список следущих станций предыдущей
            links++; //число рёбер графа увеличилось на 1
        }
    }

    public void addNext(String next) { //добавление строковой информации о следущей станции
        if (next.indexOf(";") == -1) //станция одна
            this.nextNames.add(new NextSt(next));
        else { //станций несколько (есть разделитель ;)
            String[] nexts = next.split(";");
            for (int i = 0; i < nexts.length; i++) {
                this.nextNames.add(new NextSt(nexts[i]));
            }
        }
    }

    public void addStation(Station S) { //добавление объекта следующей станции
        nextStations.add(S);
    }

    public boolean contains(Station st) { //проверка наличия станции в списке строковой информации 
        boolean l = false; //флаг наличия
        NextSt checkingSt; //переменная для хранения проверяемой станции в списке с информацией
        for (int i = 0; i < nextNames.size(); i++) {
            checkingSt = nextNames.get(i);
            if ((checkingSt.line.equals("") || checkingSt.line.equals(st.line)) && checkingSt.name.equals(st.name)) {
                l = true; //такая станция есть в списке строковой информации
                break;
            }
        }
        return l;
    }

    @Override
    public String toString() { //вывод в нужном формате
        String s = line + ":" + name;
        if (!nextStations.isEmpty()) {
            s += "_" + nextStations.get(0).line.toString() + ":" + nextStations.get(0).name.toString();
            for (int i = 1; i < nextStations.size(); i++) {
                s += ";" + nextStations.get(i).line.toString() + ":" + nextStations.get(i).name.toString();
            }
        }
        return s; 
    }
}



class base {
    public static boolean notException(String s) { //проверка на исключение
        if (s.equals("Тестовская") || s.equals("Смоленская") || s.equals("Люблино") 
        || s.equals("Новопеределкино") || s.equals("Арбатская")) {
            return false; //у станций одинаковые названия, но они не связаны
        }
        return true;
    }

    public static String bfs(ArrayList<Station> stationsList, String startStr, String endStr) { //алгоритм поиска в ширину
        //stationList - список станций, startStr - строка с информацией о начальной станции, endStr - о конечной станции 
        String[] startData = startStr.split(":"); //разбиение инфы на элементы (название и линия)
        String[] endData = endStr.split(":");

        if (startData[1].equals(endData[1]) && startData[2].equals(endData[2])) { //проверка совпадение начальной и конечной станции
            return "Вы уже на станции";
        }

        Station start = null, end = null;

        for (int i = 0; i < stationsList.size(); i++) { //поиск объектов по названию и линии станции
            Station checkingStation = stationsList.get(i);
            if (startData[1].equals(checkingStation.line) && startData[2].equals(checkingStation.name)) {
                start = checkingStation; //совпадение инфы о начальной станции с проверяемым объектом станции
            } else {
                if (endData[1].equals(checkingStation.line) && endData[2].equals(checkingStation.name)) {
                    end = checkingStation; //совпадение инфы о конечной станции с проверяемым объектом станции
                }
            }
        }
        if (start == null || end == null) { //станции не найдены
            return "Таких станций не существует";
        }
        ArrayList<ArrayList<Station>> queue = new ArrayList<ArrayList<Station>>(); //очередь из маршрутов
        ArrayList<Station> startPath = new ArrayList<Station>(); //начальный маршрут с начальной станцией
        startPath.add(start); //добавляем начальную вершину
        queue.add(startPath); //добавляем маршрут с начальной станцией

        while (!queue.isEmpty()) { //цикл поиска ближайшего маршрута
            ArrayList<Station> path = queue.get(0); //первый маршрут в очереди 
            queue.remove(0);  //удаляем из очереди
            Station station = path.get(path.size() - 1); //получаем последнюю станцию в маршруте для проверки 
            if (!station.visited) { //если станция не посещена ранее в другом маршруте
                for (Station nextStation : station.nextStations) { //перебор следующих станций к текущей
                    ArrayList<Station> newPath = new ArrayList<Station>(path); //создаём новый маршрут на основе ткущего
                    newPath.add(nextStation); //добавляем следующую станцию
                    queue.add(newPath); //добавляем маршрут в очередь

                    if (nextStation == end) { //если станция в переборе совпадает с конечной
                        nextStation.visited = true;
                        String res = "Количество станций до пункта назначения: " + (newPath.size() - 1) + "\n";
                        String line = ""; //линия по которой едем
                        Station checkingStation; //следующая станция в маршруте
                        for (int i = 0; i < newPath.size(); i++) {
                            checkingStation = newPath.get(i); //получаем следующую станцию в маршруте
                            if (line.equals("") || !line.equals(checkingStation.line)) { 
                                //если это начало движения или пересадка получаем линию
                                res += "\n\n";
                                if (!line.equals("")) res += "Пересадка: ";
                                line = checkingStation.line;
                                res += line + "\n"; //прибавляем линию к результату
                            }
                            res += "\n" + checkingStation.name; //прибавляем название станции к результату
                        }
                        return res; //возвращаем результат
                    }
                }
                station.visited = true; //отмечаем станцию как посещённую
            }
        }
        return "Станция не найдена";
    }

    public static void main(String[] args) {
        ArrayList<Station> allStations = new ArrayList<Station>(); //список со всеми станциями
        String line = ""; //линия получаемой станции
        Station lastStation = null; //предыдущая станция в линии
        String[] data; //элементы данных из файла
        try(FileReader f = new FileReader(new File("stationsWithLinks.txt"))) { //пробуем открыть файл с данными о станциях
            BufferedReader reader = new BufferedReader(f); //создаём объект буферизации
            String buffer = "";
            while((buffer = reader.readLine()) != null) { //читаем файл до конца
                if (buffer.equals("") || buffer.equals("\n") || buffer.equals(" ")) { //пустая строка
                    line = "";
                    lastStation = null;
                } else {
                    if (line.equals("")) { //линия не задана(предыдущая строка пустая)
                        line = buffer;
                        lastStation = null;
                    } else { //линия задана
                        if (buffer.indexOf("_") != -1) { //есть информация о пересадках
                            data = buffer.split("_");
                            lastStation = new Station(line, data[0], lastStation, allStations.size() + 1); //создаём станцию
                            allStations.add(lastStation); //добавляем станцию
                            lastStation.addNext(data[1]); //добавляем инфу о пересадках
                        } else { //нет информации о пересадках
                            lastStation = new Station(line, buffer, lastStation, allStations.size() + 1); //создаём станцию
                            allStations.add(lastStation); //добавляем в список станций
                        }     
                    }
                }
            }
            Station exactStation, checkingStation; //текущая и проверяемая станции
            for (int i = 0; i < allStations.size(); i++) {
                exactStation = allStations.get(i); //текущая
                for (int j = 0; j < allStations.size(); j++) {
                    if (j != i) { //если это не текущая станция
                        checkingStation = allStations.get(j); //проверяемая
                        if (notException(exactStation.name) && notException(checkingStation.name) 
                        && exactStation.name.equals(checkingStation.name)) {
                            if(!exactStation.nextStations.contains(checkingStation)) 
                                exactStation.addStation(checkingStation);
                                //добавление пересадок если у станций одинаковые названия, не являющиеся исключения
                                Station.links++;
                            if(!checkingStation.nextStations.contains(exactStation))//этой станции нет в списке следующих станций
                                checkingStation.addStation(exactStation);
                        } else {
                            if (exactStation.contains(checkingStation) || checkingStation.contains(exactStation)) { 
                                //если проверяемая или текущая станции есть в списках пересадок
                                if(!exactStation.nextStations.contains(checkingStation)) //если станции нет в списке следующих станций
                                    exactStation.addStation(checkingStation); //добавление станции в список
                                    Station.links++;
                                if(!checkingStation.nextStations.contains(exactStation))
                                    checkingStation.addStation(exactStation);                          
                            }
                        }
                    }
                }
            }

            try(FileWriter writer = new FileWriter("graph.txt")) { //Открытие файла смежностей в графе метро
                for (int i = 0; i < allStations.size(); i++) {
                    writer.write(i + " " + allStations.get(i).toString() + "\n");
                }
                writer.write("Количество рёбер в графе: " + Station.links + "\n");
                writer.close();
            }
            catch (IOException ex) { //обработка исключени при открытии файла
                System.out.println(ex.getMessage());
            }

            String res = "";
            try (FileReader in = new FileReader(new File("input.txt"))) { //получение данных о начальной и конечной станциях
                BufferedReader inner = new BufferedReader(in);
                String start = inner.readLine();
                String end = inner.readLine();
                res = bfs(allStations, start, end); //получение результата
            }
            catch(IOException ex) {
                System.out.println(ex.getMessage());
            }

            try(FileWriter outer = new FileWriter("output.txt")) { //открытие файла результата
                outer.write(res + "\n"); //вывод результата
            }
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}