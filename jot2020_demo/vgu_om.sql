INSERT INTO Student (student_id, email) VALUES 
('Hoang', 'hoang@vgu.edu.vn'),
('Chau', 'chau@vgu.edu.vn'),
('An', 'an@vgu.edu.vn'),
('Thanh', 'thanh@vgu.edu.vn'),
('Nam', 'nam@vgu.edu.vn');

INSERT INTO Lecturer (lecturer_id, email) VALUES
('Manuel', 'Manuel@vgu.edu.vn'),
('Huong', 'Huong@vgu.edu.vn'),
('Hieu', 'Hieu@vgu.edu.vn');

INSERT INTO Enrollment (students, lecturers) VALUES
('Hoang', 'Manuel'),
('Chau', 'Manuel'),
('An', 'Manuel'),
('Chau', 'Huong'),
('Thanh', 'Huong'),
('Thanh', 'Hieu'),
('Nam', 'Hieu');