class Node:
    def __init__(self,add,port,overlay):
        self.ip = add
        self.port = port
        self.overlay = overlay
        self.addressTable = {}
   
    def addToTable(self):
    	return None