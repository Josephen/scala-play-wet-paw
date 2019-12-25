# --- !Ups

CREATE TABLE users (
                       id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
                       email varchar(50) NOT NULL UNIQUE,
                       password varchar(100) NOT NULL,
                       username varchar(20) NOT NULL UNIQUE,
                       created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE articles (
                          id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          address varchar(300) NOT NULL,
                          author varchar(20) NOT NULL,
                          sex Boolean NOT NULL,
                          description varchar(300) NOT NULL,
                          contacts VARCHAR(255),
                          image VARCHAR(255),
                          category VARCHAR(255) NOT NULL,
                          approved BOOLEAN,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                          FOREIGN KEY (author) REFERENCES users(username)
);
# --- !Down
DROP TABLE users;
DROP TABLE articles;