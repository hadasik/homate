from google.appengine.ext import db
from google.appengine.api import datastore_errors
from google.appengine.ext.webapp.util import run_wsgi_app
from datetime import datetime
import math
import time
import ast, json, pdb, webapp2, operator	

class UsersDB(db.Model):
	user_name = db.StringProperty(required=True)
	user_password = db.StringProperty(required=True)
	is_admin = db.BooleanProperty()
	user_groupID = db.IntegerProperty(default = 0)

class GroupsDB(db.Model):
	groupID = db.IntegerProperty (indexed = True,default = 0)
	group_name = db.StringProperty(required=True)
	group_admin = db.StringProperty(required=True)

class PaymentsDB(db.Model):
	bill_id = db.IntegerProperty(required=True,default = 0)
	group_id = db.IntegerProperty(required=True)
	category = db.StringProperty(choices = ('water', 'gas', 'electricity',
		'groceries', 'internet', 'TV', 'municipals', 'building fee','other'))
	bill = db.IntegerProperty(required=True)
	date = db.StringProperty(required=True)
	is_payed = db.BooleanProperty(required=True, default = False)

class UserPaymentsDB(db.Model):
	bill_id = db.IntegerProperty(required=True,default = 0)
	user_name = db.StringProperty(required=True)
	status = db.IntegerProperty(required=True,default = 0)
# 0 = not payed, 1 = payed,unverifyed, 2 - payed, verifyed

class ShopListDB(db.Model):
	group_id = db.IntegerProperty(required=True)
	product = db.StringProperty()

class CleaningGroupDB(db.Model):
	groupID = db.IntegerProperty(required=True)
	start_time = db.IntegerProperty(required=True)
	days = db.IntegerProperty(required=True)

class CleaningOrderDB(db.Model):
	groupID = db.IntegerProperty(required=True)
	order = db.IntegerProperty(required=True)
	username = db.StringProperty(required=True)


match = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ", x='counter')
user=match.get()
if user ==None:
	UsersDB(user_name = 'counter',user_password = '1111',is_admin = True,user_groupID = -1).put()
	GroupsDB(group_admin = 'counter',group_name = 'house of counters',groupID = 1).put()

class MainPage(webapp2.RequestHandler):
	def get(self):
		self.response.out.write("<html><body>")
		self.response.out.write("<H1>'welcome to homate testing server'</H1>")

	def post(self):
		dba=dataBaseClass()
		m = eval(self.request.body)
		n = json.dumps(m)
		o = json.loads(n)
		action = o['action']

		if 'new_user_registry' in action:
			userName = o['username']
			passWord = o['password']
			msg = dba.new_user_registry(userName,passWord)
			result = {'action' : action,'return value' : msg[0],
			'msg' : msg[1], 'data':msg[2],'data2' : msg[3]}

		elif 'get_group' in action:
			userName = o['username']
			msg = dba.get_group(userName)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'create_new_group' in action:
			userName = o['username']
			msg = dba.create_new_group(userName)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1], 'data':msg[2]}

		elif 'edit_group_name' in action:
			group_id = o['groupID']
			groupName = o['groupName']
			msg = dba.edit_group_name(group_id, groupName)
			result = {'action' : action,'return value' : msg}
		elif 'add_member' in action:
			userName = o['username']
			memberName = o['memberName']
			msg = dba.add_new_member(userName,memberName)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'remove_member' in action:
			userName = o['username']
			memberName = o['memberName']
			msg = dba.remove_member(userName,memberName )
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'leave_group' in action:
			userName = o['username']
			msg = dba.leave_group(userName)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'get_members' in action:
			groupID = o['groupID']
			msg = dba.get_members(groupID)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'set_admin' in action:
			username = o['username']
			newAdmin = o['newAdmin']
			msg = dba.set_admin(username,newAdmin )
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'get_shopping_list' in action:
			groupID = o['groupID']
			msg = dba.get_shopping_list(groupID)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'remove_shopping_item' in action:
			groupID = o['groupID']
			username = o['username']
			items = o['items']
			msg = dba.remove_shopping_item(groupID, username,items)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'add_shopping_item' in action:
			groupID = o['groupID']
			username = o['username']
			item = o['item']
			msg = dba.add_shopping_item(groupID, username,item)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'show_archive' in action:
			groupID = o['groupID']
			category = o['category']
			msg = dba.show_archive(groupID, category)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'add_bill' in action:
			groupID = o['groupID']
			category = o['category']
			username = o['username']
			bill = o['bill']
			date = o['date']
			msg = dba.add_bill(groupID, username, category, bill, date)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'show_bill' in action:
			groupID = o['groupID']
			category = o['category']
			msg = dba.show_bill(groupID, category)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'get_bills_total' in action:
			groupID = o['groupID']
			username = o['username']
			msg = dba.get_total(username, groupID)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'set_payment' in action:
			admin_name = o['admin_name']
			username = o['username']
			bill_id = o['bill_id']
			status = o['status']
			msg = dba.set_payment(admin_name, username,bill_id,status)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}
		
		elif 'remove_payment' in action:
			username = o['username']
			bill_id = o['bill_id']
			groupID = o['groupID']
			msg = dba.remove_payment(username,bill_id,groupID)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'show_user_bills' in action:
			username = o['username']
			groupID = o['groupID']
			msg = dba.show_user_bills(username, groupID)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'request_approval' in action:
			username = o['username']
			bill_id = o['bill_id']
			msg = dba.request_approval(username,bill_id)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'get_payment_details' in action:
			bill_id = o['bill_id']
			msg = dba.get_payment_details(bill_id)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'get_personal_bills' in action:
			username = o['username']
			groupID = o['groupID']
			msg = dba.get_personal_bills(username, groupID)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'get_cleaning_order' in action:
			groupID = o['groupID']
			msg = dba.get_cleaning_order(groupID)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		elif 'set_cleaning_prefs' in action:
			groupID = o['groupID']
			username = o['username']
			days = o['days']
			order = o['order']
			msg = dba.set_cleaning_prefs(groupID, username,days, order)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1]}

		self.response.headers['Content-Type'] = 'application/JSON'
		self.response.out.write(json.dumps(result))

class dataBaseClass:

	def get_cleaning_order (self, groupID):
		ans = ""
		match2 = db.GqlQuery("SELECT * " "FROM CleaningGroupDB " "WHERE groupID =:x ",x = int(groupID))
		details=match2.get()
		if details == None:
			return 0,ans
		else:
			match3 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_groupID =:x ",x=int(groupID))
			group = match3.fetch(100)
			numOfMembers = len(group)
			today = math.ceil(time.time() / (60*60*24))
			start_of_routine = details.start_time
			days = details.days
			curr = (math.floor((today - start_of_routine)/days))%numOfMembers
			i= 0
			tmp = True
			while (tmp and i < numOfMembers):
				match1 = db.GqlQuery("SELECT * " "FROM CleaningOrderDB " "WHERE groupID =:1 AND order=:2",
									int(groupID), int(((i+curr)%numOfMembers)))
				member=match1.get()
				if member == None :
					tmp = False
				else:	

					ans = ans + member.username + "#"
					i =+1
		return 1,ans

	def set_cleaning_prefs(self, groupID, username,days, order):
		order = order.split("#")
		match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=username)
		member = match1.get()
		if member.is_admin == True:
			match = db.GqlQuery("SELECT * " "FROM CleaningGroupDB " "WHERE groupID =:x ",x = int(groupID))
			details=match.get()
			t = math.ceil(time.time() / (60*60*24))
			if details == None: 
				CleaningGroupDB(groupID = int(groupID),days = int(days),start_time = int(t)).put()
				for i in range (0, len(order)-1):
					CleaningOrderDB(groupID = int(groupID), username = order[i], order = i).put()
			else:
				details.days = int(days)
				details.start_time = int(t)
				details.put()
				for i in range (0, len(order)-1):
					match2 = db.GqlQuery("SELECT * " "FROM CleaningOrderDB " "WHERE groupID =:1 AND username =:2",
											int(groupID), order[i])
					details=match2.get()
					if details == None:
						CleaningOrderDB(groupID = int(groupID), username = order[i], order = i).put()
					else:
						details.order = i
						details.put()
			return 1, "set cleaning preferences successfully"
		else:
			return 0, "you are not permited to set preferences"

		

	def get_payment_details(self,bill_id):
		ans = ""
		match1 = db.GqlQuery("SELECT * " "FROM PaymentsDB " "WHERE bill_id =:x ",x = int(bill_id))
		details=match1.get()
		match2 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_groupID =:x", x = details.group_id)
		members=match2.fetch(1000)
		
		ans = ans + "for date: " + details.date + " total: " + str(details.bill) + "@"
		ans = ans +str(bill_id) +"@"
		for m in range (0,len(members)):
			match3 = db.GqlQuery("SELECT * " "FROM UserPaymentsDB " "WHERE user_name =:1 AND bill_id =:2",
									members[m].user_name, int(bill_id))
			user=match3.fetch(1000)
			ans += "" + members[m].user_name + "@"
			if len(user) == 0 :
				ans +=  "0" + "@"
			else:
				ans += ""+ str(user[0].status) + "@"
		return 1, ans

	def get_personal_bills(self,username,group_id):
		ans = ""
		match1 = db.GqlQuery("SELECT * " "FROM PaymentsDB "
							 "WHERE group_id =:1 AND is_payed =:2 ",int(group_id), False)
		bills=match1.fetch(1000) #all the unpayed bills of the group
		match2 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_groupID =:x", x = int(group_id))
		members=match2.fetch(1000) #all group members

		for i in range (0,len(bills)):
			curr_bill = bills[i].bill_id
			match3 = db.GqlQuery("SELECT * " "FROM UserPaymentsDB " "WHERE user_name =:1 AND bill_id =:2",
										username, int(curr_bill) )
			curr=match3.get() #curr bill and user details
			if curr ==None or curr.status == 0:
				ans = ans + bills[i].category+" - for date: " + bills[i].date + " total: " + str(bills[i].bill) + "@"
				ans = ans +str(bills[i].bill_id) +"@"
				for member in members:
					match4 = db.GqlQuery("SELECT * " "FROM UserPaymentsDB " "WHERE user_name =:1 AND bill_id =:2",
											member.user_name, bills[i].bill_id )
					user=match4.get()
					ans += "" + member.user_name + "@"
					if user == None:
						ans +=  "0" + "@"
					else:
						ans += ""+ str(user.status) + "@"
				ans += "#"
		return 1, ans

	def remove_payment(self,username,bill_id,group_id):
		match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=username)
		member = match1.get()
		if member.is_admin == True and member.user_groupID == int(group_id):
			match2 = db.GqlQuery("SELECT * " "FROM PaymentsDB " "WHERE bill_id =:x ",x=int(bill_id))
			payment=match2.get()
			payment.delete()
			return 1,"bill was removed successfuly"
		else:
			return 0, "you are not permitted to remove a bill"

	def show_user_bills(self, username, groupID):
		ans = ""
		match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_groupID =:x", x = int(groupID))
		members=match1.fetch(1000)
		numOfMembers = len(members)

		match2 = db.GqlQuery("SELECT * " "FROM PaymentsDB "
							 "WHERE group_id =:1 AND is_payed =:2 ",int(groupID), False)
		bills=match2.fetch(1000)

		for i in range (0,len(bills)):
			match3 = db.GqlQuery("SELECT * " "FROM UserPaymentsDB " "WHERE user_name =:1 AND bill_id =:2",
									username, bills[i].bill_id )
			user_bill=match3.get()
			if user_bill == None or user_bill.status == 0:
				bill = "%.2f" %bills[i].bill/float(numOfMembers)
				ans =ans+"for date: "+bills[i].date+" total: "+  bill +"@"
		return 1, ans

	def add_to_archive(self, group_id, bill_id):
		
		match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_groupID =:x ",x=int(group_id))
		members1=match1.fetch(100)
		match2 = db.GqlQuery("SELECT * " "FROM UserPaymentsDB " "WHERE bill_id =:x ",x=int(bill_id))
		members2=match2.fetch(100)
		if len(members1)==len(members2):
			
			for m in range (0,len(members2)):
				if members2[m].status != 2:
					return 
			match3 = db.GqlQuery("SELECT * " "FROM PaymentsDB " "WHERE bill_id =:x ",x=int(bill_id))
			payment=match3.get()
			payment.is_payed = True
			payment.put()
		return

	def show_archive(self, group_id, category):

		ans = ""
		match = db.GqlQuery("SELECT * " "FROM PaymentsDB " "WHERE group_id =:1 AND category =:2 AND is_payed =:3 ",
							 int(group_id), category, True)
		bills = match.fetch(1000)
		for bill in bills:
			ans += "for date: " + str(bill.date) + " total: " + str(bill.bill) + "#"
		return 1,ans

	def add_bill(self, group_id, user_name,category, bill, date):

		match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=user_name)
		member = match1.get()
		if member.is_admin == True and member.user_groupID == int(group_id):
			match2 = db.GqlQuery("SELECT * " "FROM PaymentsDB ")
			payments = match2.fetch(1000)
			bill_count = len(payments) + 1
			PaymentsDB(bill_id = bill_count, group_id = int(group_id), 
						category = category, date =date, bill = int(bill)).put()
			return 1,"bill was added successfuly"
		else:
			return 0, "you are not permitted to add new bill"


	def show_bill(self, group_id,category):
		ans = ""
		match1 = db.GqlQuery("SELECT * " "FROM PaymentsDB "
							 "WHERE group_id =:1 AND category =:2 AND is_payed =:3 ",int(group_id), category, False)
		bills=match1.fetch(1000)
		match2 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_groupID =:x", x = int(group_id))
		members=match2.fetch(1000)

		for i in range (0,len(bills)):
			ans = ans + "for date: " + bills[i].date + " total: " + str(bills[i].bill) + "@"
			ans = ans +str(bills[i].bill_id) +"@"
			for member in members:
				match3 = db.GqlQuery("SELECT * " "FROM UserPaymentsDB " "WHERE user_name =:1 AND bill_id =:2",
										member.user_name, bills[i].bill_id )
				user=match3.get()
				ans += "" + member.user_name + "@"
				if user == None:
					ans +=  "0" + "@"
				else:
					ans += ""+ str(user.status) + "@"
			ans += "#"
		return 1, ans

	def get_total(self, username, group_id):

		total_sum = 0

		match2 = db.GqlQuery("SELECT * " "FROM PaymentsDB " "WHERE group_id =:1 AND is_payed =:2", int(group_id),False)
		bills=match2.fetch(1000)
		match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_groupID =:x", x = int(group_id))
		members=match1.fetch(1000)
		numOfMembers = len(members)
		
		for i in range (0,len(bills)):
			match3 = db.GqlQuery("SELECT * " "FROM UserPaymentsDB " "WHERE user_name =:1 AND bill_id =:2",
										username, bills[i].bill_id )
			payment = match3.get()
			if payment==None or payment.status == 0:
				total_sum += bills[i].bill / float(numOfMembers)

		return 1, "your bills sums in " + str("%.2f" %total_sum) + ""

	def set_payment(self,admin_name, username,bill_id,status):
		match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x", x = admin_name)
		admin=match1.get()
		if admin.is_admin == False:
			return 0, "you are not permitted to set payments"
		else:
			match2 = db.GqlQuery("SELECT * " "FROM UserPaymentsDB " "WHERE bill_id =:1 AND user_name =:2",
								int(bill_id), username)
			payment=match2.get()
			payment.status = int(status)
			payment.put()

			self.add_to_archive(admin.user_groupID, bill_id)
			return 1, "status updated"


	def request_approval(self, username,bill_id):
		match1 = db.GqlQuery("SELECT * " "FROM UserPaymentsDB " "WHERE bill_id =:1 AND user_name =:2",
								bill_id, username)
		payment=match1.get()
		if payment == None:
				UserPaymentsDB(bill_id = int(bill_id), user_name = username,status = 1).put()
		else:
			 payment.status = 1
			 payment.put()


		return 1, "payment noted"


	def get_shopping_list(self, groupID):
		try:
			match = db.GqlQuery("SELECT * " "FROM ShopListDB " "WHERE group_id =:x ",x=groupID)
			items=match.fetch(100)
			itemsList = ""
			for i in range (0,len(items)):
				itemsList+=items[i].product
				itemsList+='#'
			return 1, itemsList
		except datastore_errors,e:
			return 0,'getting items list failed : '+e,''

	def remove_shopping_item(self, groupID,username,items):
		try:
			match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=username)
			user=match1.get()
			if user.user_groupID != int(groupID):
				return 0, "you are not a member in this group anymore"
			else:
				items_to_remove = items.split('#')
				match2 = db.GqlQuery("SELECT * " "FROM ShopListDB " "WHERE group_id =:x ",x=groupID)
				group_items=match2.fetch(100)
				for item in items_to_remove:
					for i in group_items:
						if i.product == item:
							i.delete()
				return self.get_shopping_list(groupID)
		except datastore_errors,e:
			return 0,'removing failed : '+e,''

	def add_shopping_item(self, groupID,username,item):
		try:
			match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=username)
			user=match1.get()
			if user.user_groupID != int(groupID):
				return 0, "you are not a member in this group anymore"
			elif '#' in item:
				return 0, '# is not allowed'
			else:
				ret = ShopListDB(group_id = groupID, product = item).put()
				return 1,"adding succeed"

		except datastore_errors,e:
			return 0,'adding failed : '+e,''

	def add_new_member(self,userName,memberName):
		try:
			match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=userName)
			user=match1.get()
			if user.is_admin == False:
				return 0, "you are not permitted to add new members"
			else:
				match2 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=memberName)
				member=match2.get()
				if member == None:
					return 0, "no such user"
				elif member.user_groupID == 0:
					member.user_groupID = user.user_groupID
					member.put()
					return 1,"member was added successfuly"
				else:
					return 0,"user is already member in a group"
		except datastore_errors,e:
			return 0,'adding failed : '+e,''

	def remove_member(self,userName,memberName):
		if userName == memberName:
			return self.leave_group(userName)
		else:
			try:
				match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=userName)
				user=match1.get()
				if user.is_admin == False:
					return 0, "you are not permitted to remove members"
				else:
					match2 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=memberName)
					member=match2.get()
					if member == None:
						return 0, "no such user"
					elif member.user_groupID == user.user_groupID:
						member.user_groupID = 0
						member.put()
						return 2,"member removed"
					else:
						return 0,"user is not a member anymore"
			except datastore_errors,e:
				return 0,'removing failed : '+e,''

	def leave_group(self,userName):
		try:
			match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=userName)
			user1=match1.get()
			if user1.is_admin == False:
				user1.user_groupID = 0
				user1.put()
				return 1, "left group successfuly"

			else:
				match2 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_groupID =:x ",
										x=user1.user_groupID)
				user2=match2.fetch(100)
				if len(user2) == 1:
					user1.user_groupID = 0
					user1.is_admin = False
					user1.put()

					match2 = db.GqlQuery("SELECT * " "FROM GroupsDB " "WHERE group_admin =:x ",
											x=userName)
					match2.get().delete()

					return 1, "left group successfuly"
				else:
					return 0, "you must pass your admin permissions first"
		except datastore_errors,e:
			return 0,'leaving failed : '+e,''

	def get_members(self,groupID):
		try:
			match = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_groupID =:x ",x=int(groupID))
			users=match.fetch(100)
			membersList = ""
			for i in range (0,len(users)):
				membersList+=users[i].user_name
				membersList+='#'
			return 1, membersList
		except datastore_errors,e:
			return 0,'getting member list failed : '+e,''


	def set_admin(self,username,newAdmin):
		try:
			match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=username)
			user1=match1.get()
			if user1.is_admin == False:
				return 0, "you are not permitted to set admin"
			else:
				match2 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=newAdmin)
				user2=match2.get()
				if user2.user_groupID == user1.user_groupID:
					match3 = db.GqlQuery("SELECT * " "FROM GroupsDB " "WHERE group_admin =:x ",x=username)
					user3 = match3.get()
					user3.group_admin = newAdmin
					user3.put()
					user2.is_admin = True
					user1.is_admin = False
					user1.put()
					user2.put()
					return 1, "admin was changed successfuly"
		except datastore_errors,e:
			return 0,'getting member list failed : '+e,''



	def new_user_registry(self,username,password):
		try:
			match = db.GqlQuery("SELECT * "
				"FROM UsersDB "
				"WHERE user_name =:x ",
				x=username)
			user=match.get()
			if user == None:
				if '#' in username:
					return 0, '# is not allowed', 0, "House Of Fun"
				else:
					UsersDB(user_name = username,user_password = password,
					is_admin = False).put()
					return 1,username+' was added successfully', 0, "House Of Fun"
			
			elif user.user_password == password:
				match2 = db.GqlQuery("SELECT * " "FROM GroupsDB " "WHERE groupID =:x ", x=user.user_groupID)
				group=match2.get()
				if group == None:
					return 1,username+' Welcome Back!', user.user_groupID, "House Of Fun"
				else:
					return 1,username+' Welcome Back!', user.user_groupID, group.group_name
			else:
				return 0,'Wrong Password','',"House Of Fun"
		except datastore_errors,e:
			return 0,'adding failed : '+e,'',"House Of Fun"

	def get_group(self, username):

			match1 = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ", x=username)
			user=match1.get()
			
			if user.user_groupID != 0:
				match2 = db.GqlQuery("SELECT * " "FROM GroupsDB " "WHERE groupID =:x ", x=user.user_groupID)
				group=match2.get()
				return user.user_groupID, group.group_name
			else:
				return user.user_groupID, "House Of Fun"

	def edit_group_name(self, group_id, groupName):
			try:
				match = db.GqlQuery("SELECT * " "FROM GroupsDB " "WHERE groupID =:x ", x=int(group_id))
				group=match.get()
				group.group_name = groupName
				group.put()
				return 1
			except datastore_errors,e:
				return 0

	def create_new_group(self, adminName):
		match3 = db.GqlQuery("SELECT * " "FROM GroupsDB " "WHERE group_admin =:x ",x='counter')
		counter = match3.get()
		count = counter.groupID
		try:
			match1 = db.GqlQuery("SELECT * " "FROM GroupsDB " "WHERE user_name =:x ",x=adminName)
			admin1 = match1.get()
			if admin1==None:

				try:
					match = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=adminName)
					admin = match.get()
					if admin.user_groupID == 0:
						GroupsDB(group_name = "house of fun",
							group_admin = adminName,groupID = count).put()
						counter.groupID = count + 1
						counter.put()
						admin.user_groupID = count
						admin.is_admin = True
						admin.put()
						return 1, 'new group crated successfully', admin.user_groupID
					else: return 0,'adding failed : ',0

				except datastore_errors,e:
					return 0,'adding failed : '+e,0
			else:
				return 0,'one group allowed : ',admin1.groupID
		except datastore_errors,e:
			return 0,'adding failed : '+e,0

	def add_to_group(self, admin_username, new_member):
		try:
			match_admin = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x = admin_username)
			admin = match_admin.get()
			if admin!=None:
				try:
					match_member = db.GqlQuery("SELECT * " "FROM UsersDB "
										 "WHERE user_name =:x ",x = new_member)
					member = match_member.get()
					if member.user_groupID == admin.user_groupID:
						return 0,"member has already added to this group",''
					elif member.user_groupID != None:
						return 0,"user is already a member of another group",''
					else:
						member.user_groupID = admin.user_groupID
						member.put()
						return 1,new_member+" has been successfuly added to: " + admin.user_groupID,''
				except datastore_errors,e:
					return 0,'adding failed : '+e,''
		except datastore_errors,e:
			return 0,'adding failed : '+e,''

app = webapp2.WSGIApplication([('/', MainPage)],debug=True)



'''	def set_group(self, adminName, groupName):
		return 1
		

				return 1,groupName+" roomate group was created successfully",''
			else:
				return 0,adminName+" already created a group",''

		except datastore_errors,e:
			return 0,'adding failed : '+e,''
'''