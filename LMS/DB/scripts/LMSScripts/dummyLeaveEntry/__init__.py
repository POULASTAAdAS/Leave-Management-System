leaveIdStr = '''
1
2
3
4
5
6
7
8
9
10
11
12
13
'''
leaveIdList = leaveIdStr.split()

CONST_QUERY = 'Insert ignore into LeaveBalance (teacherId , teacherTypeId , leaveTypeId , leaveBalance) values\n'

with open('DummyLeaveBalance.sql', 'a') as l:
    l.write(CONST_QUERY)
    for teacherId in range(2, 4):
        for leaveId in leaveIdList:
            l.write(f"({teacherId} , {1} , {leaveId} , {14.00 * int(leaveId)}),\n")
