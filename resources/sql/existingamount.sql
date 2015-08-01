-- select the item of a player identified by symbol if it exists.
SELECT *
FROM item
WHERE id_player = :idplayer AND symbol = :symbol
