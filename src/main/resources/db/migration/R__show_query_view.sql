CREATE
OR REPLACE VIEW
       show_details
AS
SELECT s.id,
       s.start_time,
       s.ticket_price_amount,
       s.ticket_price_currency,
       s.movie_id,
       m.title AS movie_title
FROM show s
         LEFT JOIN movie m ON s.movie_id = m.id;