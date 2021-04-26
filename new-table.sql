DROP TABLE IF EXISTS cart;
CREATE table cart (
	salesId integer NOT NULL AUTO_INCREMENT,
	qty integer NOT NULL,
	FOREIGN KEY (salesId)
		REFERENCES sales(id)
);

