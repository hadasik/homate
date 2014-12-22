from google.appengine.ext import db
from google.appengine.api import datastore_errors
from google.appengine.ext.webapp.util import run_wsgi_app
from datetime import datetime
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
	group_id = db.StringProperty(required=True)
	category = db.StringProperty(choices = ('water', 'phone', 'gas', 'electricity',
		'groceries ', 'rent', 'Internet', 'TV', 'municipal_tax', 'building_committee'))
	bill = db.IntegerProperty()
	date = db.DateProperty()

class ShopListDB(db.Model):
	group_id = db.StringProperty(required=True)
	product = db.StringProperty()

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
			result = {'action' : action,'return value' : msg[0],'msg' : msg[1], 'data':msg[2],'data2' : msg[3]}
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


		self.response.headers['Content-Type'] = 'application/JSON'
		self.response.out.write(json.dumps(result))

class dataBaseClass:

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
					user3.adminName = newAdmin
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