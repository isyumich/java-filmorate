# java-filmorate
Template repository for Filmorate project.
<picture>
  <img alt="Shows an illustrated sun in light mode and a moon with stars in dark mode." src="https://github.com/isyumich/java-filmorate/blob/3a30fb5db64a041f8dfcf4896fec64a9a4869e0f/resources/Schema_DataBase.jpg?raw=true">
</picture>

Так как список друзей и список фильмов, у которых проставлен лайки - связь 
"многие ко многим", то решил выделить две отдельные таблицы: FriendList и 
FilmLikeList. Далее, с помощью sql можно будет вытягивать нужные нам данные.
Например, первые 10 самых популярных фильмов:

select film_id,
       count(user_id) as count_like
from FilmLikeList
group by film_id
order by count_like desc
limit 10

И далее приджойнившись к таблице Film можем получить нужную нам информацию по
этим фильмам.
