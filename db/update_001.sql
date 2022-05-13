CREATE TABLE IF NOT EXISTS employees(
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    inn serial NOT NULL,
    hired TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
    );
create table person (
                        id serial primary key not null,
                        login varchar(2000),
                        password varchar(2000)
);
insert into employees (name, surname, inn) values ('ivan', 'ivanov', '24234234');
insert into person (login, password) values ('parsentev', '123');
insert into person (login, password) values ('parsentevsfsdf', '123sdfsf');
insert into employees_accounts (employee_id, accounts_id) values ('1', '1');
insert into employees_accounts (employee_id, accounts_id) values ('1', '2');
insert into employees_accounts (employee_id, accounts_id) values ('1', '3');