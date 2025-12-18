CREATE TABLE IF NOT EXISTS employees
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name    VARCHAR(100)   NOT NULL,
    last_name     VARCHAR(100)   NOT NULL,
    position      VARCHAR(100)   NOT NULL,
    salary        NUMERIC(19, 2) NOT NULL,
    department_id UUID           NOT NULL,

    CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id)
            REFERENCES departments (id)
            ON DELETE RESTRICT
);