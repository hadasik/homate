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


		self.response.headers['Content-Type'] = 'application/JSON'
		self.response.out.write(json.dumps(result))

class dataBaseClass:
	def new_user_registry(self,username,password):
		try:
			match = db.GqlQuery("SELECT * "
				"FROM UsersDB "
				"WHERE user_name =:x ",
				x=username)
			user=match.get()
			if user == None:
				UsersDB(user_name = username,user_password = password,
					is_admin = False).put()

				return 1,username+' was added successfully', 0, "House Of Fun"
			elif user.user_password == password:

				match2 = db.GqlQuery("SELECT * " "FROM GroupsDB " "WHERE groupID =:x ", x=user.user_groupID)
				group=match2.get()
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
					match_member = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x = new_member)
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