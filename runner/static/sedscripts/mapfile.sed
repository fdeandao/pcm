/GDB Face manager by Jenkey1002/d
/^$/d
/^#/d
s/'/''/g
s/"/'/g
s/'$/', '#'/g
s/#.*/,'&'/g
s/.*/INSERT INTO PLAYERMAP (ID, FACEFILE, HAIRFILE, REALNAME) VALUES (&);/g