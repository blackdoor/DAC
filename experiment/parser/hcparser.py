import re
import node

def main():
	testlog = "test.log"
	addlist = {}
	dups = []
	addingregex = re.compile("\/([0-9]+\.[0-9]+\.[0-9]+\.[0-9]+)\:([0-9]+)\s([0-9,A-F]+\:[0-9,A-F]+\:[0-9,A-F]+\:[0-9,A-F]+)", re.IGNORECASE)	
	f = open("headcount.log","r")
	for line in f:
		m = re.match(addingregex,line)
		if m is not None: 
			if m.group(1) not in addlist.keys():
				addlist[m.group(1)] = []
				addlist[m.group(1)].append(m.group(2))
			else:
				if m.group(2) not in addlist[m.group(1)]:
					addlist[m.group(1)].append(m.group(2))
				else:
					dups.append(m.group(1)+" " +m.group(2))

	print " ADDLIST ::::::::" + str(len(addlist))
	for x in addlist.keys():
		out = x + " : -- "+ str(len(addlist[x])) +"\n"
		for y in addlist[x]:
			out += "\t "+ y +" \n"
		print out

	print " FUPPPPPS ::::::::"  + str(len(dups))
	for x in dups:
		print x

	network = {}
	for x in addlist.ke
		node = node.Node(m.group(1),m.group(2),m.group(3))


	return None


main()