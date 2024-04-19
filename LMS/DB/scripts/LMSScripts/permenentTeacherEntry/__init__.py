emailFile = "D:/LMS/DB/permenetTeacherEmail.txt"

CONST_QUERY = 'insert ignore into Teacher (email , teacherTypeId) values\n'

with open('PermanentTeacherEntry.sql', 'a', encoding='utf-8') as sql:
    sql.write(CONST_QUERY)
    with open(emailFile, 'r', encoding='utf-8') as emails:
        for email in emails:
            email = email.strip()
            query = f"('{email}' , {1}),"
            sql.write(query + '\n')
