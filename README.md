# Отслеживание популярных тем, постов и товаров в потоке данных на Apache Flink

Popular-topics-tracking представляет собой реализацию проекта «Отслеживание популярных тем, постов и товаров в потоке данных на Apache Flink» в рамках зимней школы [CompTech School 2022](https://comptechschool.com/).

- docs - документация проекта

- src - код проекта

- pom.xml - фреймворк для автоматизации сборки проекта

## Назначение

Реализация эффективной по памяти версии аппаратора, которая отслеживает наиболее популярные элементы в потоке данных. 

## Принцип работы

Используя Apache Flink, оптимизируется отслеживание наиболее популярные элементы в потоке данных. Для этого используются запросы TOP-N. В программе запросы Top-N запрашивают N наибольших значений потоковой таблицы. В первом варианте реализации используется дефолтная реализация TOP-N. Во втором варианте - используются "sketches" (элементарные рандомизированные структуры данных). Для сравнения двух реализаций проводились тесты функционала, тесты  по времени и памяти для TOP-N (sketch vs default). В проекте сравниваются два метода по производительности и скорости обработки потока данных.



![image](https://user-images.githubusercontent.com/98398064/152352936-4a7a607f-df7f-463e-bde0-13e51bc0ece7.png)

## Пользователи продукта

Разработчики в области Data Science, которым требуется получать популярные элементы из потока данных.

## Установка и настройка

Запустить докер? 

В докере содержится необходимо окружение, включающее

- [Apache Kafka](https://kafka.apache.org/)

- [Apache Flink](https://flink.apache.org/)

- [Apache Cassandra](https://cassandra.apache.org/)

### Зависимости

kafka-flink(default impl topn)-cassandra

kafka-flink(sketch impl topn)-cassandra

## Использование

Подаем поток любых данных. Получаем базу данных с итоговыми результатами.

*** тут будут результаты тестов?

## Команда

- Екименко Евгений – разработчик

- Попов Дмитрий – разработчик

- Кононова Полина – технический писатель

## Кураторы

Рене Андреасович ван Беверн - эксперт, Новосибирский Исследовательский центр ООО "Техкомпания Хуавей"

Антон Георгиевич Логинов - старший инженер, Новосибирский Исследовательский центр ООО "Техкомпания Хуавей"



