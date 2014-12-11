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
	#groupID = db.StringProperty(required=True,auto_now_add=True)
	groupID = db.IntegerProperty (indexed = True,default = 1)
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

class MainPage(webapp2.RequestHandler):
	def get(self):
		self.response.out.write("<html><body>")
		self.response.out.write("<H1>'welcome to homate testing server'</H1>")
		dba=dataBaseClass()
		msg = dba.new_user_registry('counter', '1111')
		self.response.out.write("<p>" + msg[1] + "</p>")
		msg = dba.create_new_group('counter')
		self.response.out.write("<p>" + msg[1] + "</p>")

	def post(self):
		dba=dataBaseClass()
		m = eval(self.request.body)
		n = json.dumps(m)
		o = json.loads(n)
		action = o['action']
		userName = o['username']

		if 'new_user_registry' in action:
			passWord = o['password']
			msg = dba.new_user_registry(userName,passWord)
			result = {'action' : action,'return value' : msg[0],'msg' : msg[1], 'data':msg[2]}
		elif 'get_group' in action:
			msg = dba.get_group(userName)
			result = {'action' : action,'return value' : msg}
		elif 'create_new_group' in action:
			msg = dba.create_new_group(userName)
			result = {'action' : action,'return value' : msg[0], 'msg':msg[1], 'data':msg[2]}

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

				return 1,username+' was added successfully', 0
			elif user.user_password == password:
				return 1,username+' Welcome Back!', user.user_groupID
			else:
				return 0,'Wrong Password',''
		except datastore_errors,e:
			return 0,'adding failed : '+e,''

	def get_group(self, username):
			match = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ", x=username)
			user=match.get()
			return user.user_groupID

	def create_new_group(self, adminName):
		try:
			match1 = db.GqlQuery("SELECT * " "FROM GroupsDB " "WHERE user_name =:x ",x=adminName)
			admin1 = match1.get()
			if admin1==None:

				try:
					match = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=adminName)
					admin = match.get()
					if admin.user_groupID == 0:
						GroupsDB(group_name = "house of fun",group_admin = adminName).put()
						match2 = db.GqlQuery("SELECT * " "FROM GroupsDB " "WHERE group_admin =:x ",x=adminName)
						admin_group = match2.get()
						admin.user_groupID = admin_group.groupID
						admin.is_admin = True
						admin.put()
						return 1, 'new group crated successfully', 5
					else: return 0,'adding failed : '+e,0

				except datastore_errors,e:
					return 0,'adding failed : '+e,0
			else:
				return 0,'one group allowed : '+e,admin1.groupID
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