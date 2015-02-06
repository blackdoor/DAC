#import node
import re
	
def join(source):
	return None

def insert():
	return None

def regexDict(rpccode):
	return {
		'add': '.*?Adding.*?\[.*?\=\s+?(\d+\.\d+\.\d+\.\d+)\:(\d+)\,\s+?\w+?\=([0-9,a-z]+\:[0-9,a-z]+\:[0-9,a-z]+\:[0-9,a-z]+)',
		'remove': '.*?Removing.*?\[.*?\=\s+?(\d+\.\d+\.\d+\.\d+)\:(\d+)\,\s+?\w+?\=([0-9,a-z]+\:[0-9,a-z]+\:[0-9,a-z]+\:[0-9,a-z]+)',
	}[x]

def experimentSource(sourceip,source,action):
	src = 

	return None 
def main():
	# addingregex = re.compile(ur, re.IGNORECASE)	
	# removingregex = re.compile(ur'.*?Removing.*?\[.*?\=\s+?(\d+\.\d+\.\d+\.\d+)\:(\d+)\,\s+?\w+?\=([0-9,a-z]+\:[0-9,a-z]+\:[0-9,a-z]+\:[0-9,a-z]+)', re.IGNORECASE)	
	testlog = "test.log"
	f = open(testlog, 'r')
	for line in f:
		m = re.match(addingregex, line)
		g = re.match(removingregex, line)
		if m is not None:
			print "WHATHAWT -- " + str(m.group(3))
		elif g is not None:
			print "_______ -- " + str(g.group(3))
	return None

main()