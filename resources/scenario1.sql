INSERT INTO election(Election_id, description) VALUES ('Spain2023', 'Spain2023'), ('Swiss2023-1', 'Swiss vote quarter 1');
INSERT INTO voter(Voter_id, name, ssn) VALUES ('Alice', 'Alice', 'Alicessn'), ('Bob', 'Bob', 'Bobssn'), ('Charlie', 'Charlie', 'Charliessn');
INSERT INTO vote(Vote_id, elections, voters, value) VALUES ('1', 'Spain2023', 'Alice', 3), ('2', 'Swiss2023-1', 'Alice', 4), ('3', 'Swiss2023-1', 'Bob', 1);