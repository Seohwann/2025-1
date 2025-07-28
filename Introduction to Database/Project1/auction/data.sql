--
-- PostgreSQL database dump
--

-- Dumped from database version 14.17 (Ubuntu 14.17-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.17 (Ubuntu 14.17-0ubuntu0.22.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: user_info; Type: TABLE DATA; Schema: public; Owner: s21312738
--

INSERT INTO public.user_info (userid, password, isadmin) VALUES ('seo', '123', 'N');
INSERT INTO public.user_info (userid, password, isadmin) VALUES ('hwan', '123', 'Y');
INSERT INTO public.user_info (userid, password, isadmin) VALUES ('kim', '123', 'N');
INSERT INTO public.user_info (userid, password, isadmin) VALUES ('tom', '123', 'N');


--
-- Data for Name: item; Type: TABLE DATA; Schema: public; Owner: s21312738
--

INSERT INTO public.item (itemid, category, description, condition, sellerid) VALUES ('11', 'ELECTRONICS', 'ele', 'NEW', 'kim');
INSERT INTO public.item (itemid, category, description, condition, sellerid) VALUES ('2', 'BOOKS', 'boo1', 'LIKE_NEW', 'kim');
INSERT INTO public.item (itemid, category, description, condition, sellerid) VALUES ('33', 'HOME', 'hom', 'NEW', 'seo');
INSERT INTO public.item (itemid, category, description, condition, sellerid) VALUES ('400', 'CLOTHING', 'cloths', 'ACCEPTABLE', 'kim');
INSERT INTO public.item (itemid, category, description, condition, sellerid) VALUES ('14', 'ELECTRONICS', 'tronic', 'ACCEPTABLE', 'seo');
INSERT INTO public.item (itemid, category, description, condition, sellerid) VALUES ('54', 'SPORTINGGOODS', 'golf', 'ACCEPTABLE', 'seo');
INSERT INTO public.item (itemid, category, description, condition, sellerid) VALUES ('133', 'ELECTRONICS', 'use', 'GOOD', 'kim');
INSERT INTO public.item (itemid, category, description, condition, sellerid) VALUES ('1111', 'ELECTRONICS', 'newele', 'NEW', 'kim');


--
-- Data for Name: auction; Type: TABLE DATA; Schema: public; Owner: s21312738
--

INSERT INTO public.auction (auctionid, itemid, startingprice, currentprice, highestbidder, buyitnowprice, bidstarttime, bidendtime, status) VALUES (6, '54', 0, 0, NULL, 20000, '2025-05-12 20:31:52', '2025-05-12 20:33:00', 'EXPIRED');
INSERT INTO public.auction (auctionid, itemid, startingprice, currentprice, highestbidder, buyitnowprice, bidstarttime, bidendtime, status) VALUES (1, '11', 0, 8000, 'seo', 10000, '2025-05-12 20:10:52', '2025-05-12 20:20:00', 'SOLD');
INSERT INTO public.auction (auctionid, itemid, startingprice, currentprice, highestbidder, buyitnowprice, bidstarttime, bidendtime, status) VALUES (2, '2', 1000, 20000, 'seo', 20000, '2025-05-12 20:11:32', '2025-05-12 20:15:00', 'SOLD');
INSERT INTO public.auction (auctionid, itemid, startingprice, currentprice, highestbidder, buyitnowprice, bidstarttime, bidendtime, status) VALUES (5, '14', 0, 0, NULL, 20000, '2025-05-12 20:26:34', '2025-05-12 20:28:00', 'EXPIRED');
INSERT INTO public.auction (auctionid, itemid, startingprice, currentprice, highestbidder, buyitnowprice, bidstarttime, bidendtime, status) VALUES (3, '33', 10000, 30000, 'kim', 30000, '2025-05-12 20:15:51', '2025-05-12 20:20:00', 'SOLD');
INSERT INTO public.auction (auctionid, itemid, startingprice, currentprice, highestbidder, buyitnowprice, bidstarttime, bidendtime, status) VALUES (4, '400', 0, 29000, 'seo', 30000, '2025-05-12 20:22:31', '2025-05-12 20:25:00', 'SOLD');
INSERT INTO public.auction (auctionid, itemid, startingprice, currentprice, highestbidder, buyitnowprice, bidstarttime, bidendtime, status) VALUES (7, '133', 0, 25000, 'seo', 30000, '2025-05-12 20:35:17', '2025-05-12 20:37:00', 'SOLD');
INSERT INTO public.auction (auctionid, itemid, startingprice, currentprice, highestbidder, buyitnowprice, bidstarttime, bidendtime, status) VALUES (8, '1111', 0, 27000, 'tom', 30000, '2025-05-12 20:40:52', '2025-05-12 20:42:00', 'SOLD');


--
-- Data for Name: bid; Type: TABLE DATA; Schema: public; Owner: s21312738
--

INSERT INTO public.bid (bidid, bidderid, auctionid, bidprice, bidtime, bidstatus) VALUES (2, 'seo', 2, 20000, '2025-05-12 20:14:25', 'WON');
INSERT INTO public.bid (bidid, bidderid, auctionid, bidprice, bidtime, bidstatus) VALUES (3, 'kim', 3, 30000, '2025-05-12 20:18:48', 'WON');
INSERT INTO public.bid (bidid, bidderid, auctionid, bidprice, bidtime, bidstatus) VALUES (1, 'seo', 1, 8000, '2025-05-12 20:13:35', 'WON');
INSERT INTO public.bid (bidid, bidderid, auctionid, bidprice, bidtime, bidstatus) VALUES (4, 'seo', 4, 29000, '2025-05-12 20:24:09', 'WON');
INSERT INTO public.bid (bidid, bidderid, auctionid, bidprice, bidtime, bidstatus) VALUES (5, 'seo', 7, 25000, '2025-05-12 20:36:00', 'WON');
INSERT INTO public.bid (bidid, bidderid, auctionid, bidprice, bidtime, bidstatus) VALUES (6, 'seo', 8, 25000, '2025-05-12 20:41:13', 'OUTBID');
INSERT INTO public.bid (bidid, bidderid, auctionid, bidprice, bidtime, bidstatus) VALUES (7, 'tom', 8, 27000, '2025-05-12 20:41:44', 'WON');


--
-- Data for Name: billing; Type: TABLE DATA; Schema: public; Owner: s21312738
--

INSERT INTO public.billing (billingid, itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) VALUES (1, '2', 'seo', 'kim', 20000, '2025-05-12 20:14:25', 'COMPLETED');
INSERT INTO public.billing (billingid, itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) VALUES (2, '33', 'kim', 'seo', 30000, '2025-05-12 20:18:48', 'COMPLETED');
INSERT INTO public.billing (billingid, itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) VALUES (3, '11', 'seo', 'kim', 8000, '2025-05-12 20:13:35', 'COMPLETED');
INSERT INTO public.billing (billingid, itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) VALUES (4, '400', 'seo', 'kim', 29000, '2025-05-12 20:24:09', 'COMPLETED');
INSERT INTO public.billing (billingid, itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) VALUES (5, '133', 'seo', 'kim', 25000, '2025-05-12 20:36:00', 'COMPLETED');
INSERT INTO public.billing (billingid, itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) VALUES (6, '1111', 'tom', 'kim', 27000, '2025-05-12 20:41:44', 'COMPLETED');


--
-- Name: auction_auctionid_seq; Type: SEQUENCE SET; Schema: public; Owner: s21312738
--

SELECT pg_catalog.setval('public.auction_auctionid_seq', 8, true);


--
-- Name: bid_bidid_seq; Type: SEQUENCE SET; Schema: public; Owner: s21312738
--

SELECT pg_catalog.setval('public.bid_bidid_seq', 7, true);


--
-- Name: billing_billingid_seq; Type: SEQUENCE SET; Schema: public; Owner: s21312738
--

SELECT pg_catalog.setval('public.billing_billingid_seq', 6, true);


--
-- PostgreSQL database dump complete
--

