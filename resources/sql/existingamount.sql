-- select the items of a player
SELECT *
FROM item
WHERE id_player = :idplayer AND symbol = :symbol
