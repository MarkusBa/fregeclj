-- select the items of a player
SELECT max(amount)
FROM item
WHERE id_player = :idplayer
