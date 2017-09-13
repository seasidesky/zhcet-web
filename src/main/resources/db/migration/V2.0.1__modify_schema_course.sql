ALTER TABLE zhcet.course 
  ADD category VARCHAR(255) NULL,
  ADD compulsory BIT(1) NULL,
  ADD semester INT NULL,
  ADD start_year INT NULL,
  ADD finish_year INT NULL,
  ADD credits FLOAT NULL,
  ADD branch VARCHAR(255) NULL,
  ADD type VARCHAR(1) NULL,
  ADD class_work_marks INT NULL,
  ADD mid_sem_marks INT NULL,
  ADD final_marks INT NULL,
  ADD total_marks INT NULL,
  ADD lecture_part INT NULL,
  ADD theory_part INT NULL,
  ADD practical_part INT NULL;