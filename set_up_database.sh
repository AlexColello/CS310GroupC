echo -e "[server]\nwait_timeout=28800" | sudo tee -a /etc/mysql/my.cnf
cat /etc/mysql/my.cnf
sudo service mysql restart	  
cat sql/hungry.sql
mysql --user="root" --password="" < "sql/hungry.sql"
mysql --user="root" --database="hungrydatabase" --password="" < "sql/hungryTest.sql"
mysql --user="root" --database="hungrydatabase" --password="" --execute="SELECT * FROM Users"