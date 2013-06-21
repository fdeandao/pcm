/.*(?!bin)/d
s/'/''/g
s/.*/INSERT INTO PLAYERFILE (FILENAME) VALUES ('&');/g