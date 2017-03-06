CREATE USER 'cssr_user'@'localhost' IDENTIFIED BY 'fh2017cssr';
grant usage on *.* to cssr_user@localhost;
CREATE DATABASE cssr;
grant all privileges on cssr.* to cssr_user@localhost ; 
