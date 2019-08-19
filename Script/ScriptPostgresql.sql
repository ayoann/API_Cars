------------------------------------------------------------
--        Script Postgre 
------------------------------------------------------------



------------------------------------------------------------
-- Table: Garages
------------------------------------------------------------
CREATE TABLE public.Garages(
	id                  SERIAL NOT NULL ,
	name                VARCHAR (100) NOT NULL ,
	address              VARCHAR (255) NOT NULL ,
	creation_date       DATE  NOT NULL ,
	max_cars_capacity   INT  NOT NULL  ,
	CONSTRAINT Garages_PK PRIMARY KEY (id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: Cars
------------------------------------------------------------
CREATE TABLE public.Cars(
	id                   SERIAL NOT NULL ,
	registration         VARCHAR (255) NOT NULL ,
	brand                VARCHAR (255) NOT NULL ,
	model                VARCHAR (255) NOT NULL ,
	color                VARCHAR (255) NOT NULL ,
	date_commissioning   DATE  NOT NULL ,
	price                FLOAT  NOT NULL ,
	id_Garages           INT    ,
	CONSTRAINT Cars_PK PRIMARY KEY (id)

	,CONSTRAINT Cars_Garages_FK FOREIGN KEY (id_Garages) REFERENCES public.Garages(id)
)WITHOUT OIDS;



