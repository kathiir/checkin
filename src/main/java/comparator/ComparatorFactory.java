package comparator;

import objects.MapObject;
import world.Tags;

import java.util.Comparator;
import java.util.List;

public class ComparatorFactory {


    // TODO: 13.11.2019 ниже
    /*
    в компараторе реализовать блокировуку чекина в объектах по тегам
    в которых человек не может находится по логике
    таким образом можно добавить дороги заборы
    например заблокировать чекин в разрушенном здании, заборе и тд
    фабрика для компаратора

    создать класс request который будет принимать в себя данные пользователя и его местоположение
    и уже в checkMap передавать request а не user
    request(UserData, Polygon)

    добавить паттерн команду, с помощью команды отображать процесс чекина
    создать стэк (как в задаче на сортировку) чтобы показать последовательность
    проверки в зданиях, прохождение по зданиям

     */

    public Comparator<MapObject> getComparator(MapObjectComparatorType type, List<Tags> tags) {
        if (type == MapObjectComparatorType.SIMPLE)
            return new SimpleMapObjectComparator(tags);
        if (type == MapObjectComparatorType.MEDIAN)
            return new MedianMapObjectComparator(tags);
        return null;
    }

}
