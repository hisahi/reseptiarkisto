CREATE TABLE Annos (
	id INTEGER PRIMARY KEY,
	nimi VARCHAR(100),
	ohje TEXT
);

CREATE TABLE RaakaAine (
	id INTEGER PRIMARY KEY,
	nimi VARCHAR(100)
);

CREATE TABLE AnnosRaakaAine (
	id INTEGER PRIMARY KEY,
	annos INTEGER,
	raakaaine INTEGER,
	lkm INTEGER,
	yksikko VARCHAR(10),
	jarjestys INTEGER,
	ohje TEXT,
	FOREIGN KEY (annos) REFERENCES Annos(id),
	FOREIGN KEY (raakaaine) REFERENCES RaakaAine(id)
);

