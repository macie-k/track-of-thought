import json

# script to move columns/rows in levels
# 'level' variable requires change to target level
# both files need to be in the same directory

level = 'name.level'

f_read = open(level, 'r')
data = json.load(f_read)

property = 'column'     # [column / row]
val_change = -2         # [ +-n ]


for i in data['stations']:
    new_col = int(i[property]) + val_change
    i[property] = str(new_col)

for i in data['tracks']:
    new_col = int(i[property]) + val_change
    i[property] = str(new_col)
    
f_read.close()

f_write = open(level, 'w')
f_write.write(str(data))
f_write.close()
