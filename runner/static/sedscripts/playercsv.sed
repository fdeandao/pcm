1d
s/'/''/g 
#s/[^,]*[^,]/'&'/g
s/,/'&'/g
#s/,$/,''
s/.*/INSERT INTO PLAYERCSV VALUES ('&');/g