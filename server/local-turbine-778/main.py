from google.appengine.ext import db
from google.appengine.api import datastore_errors
from google.appengine.ext.webapp.util import run_wsgi_app
from datetime import datetime
import ast, json, pdb, webapp2, operator	

class UsersDB(db.Model):
	user_name = db.StringProperty(required=True)
	user_password = db.StringProperty(required=True)
	is_admin = db.BooleanProperty()
	user_groupID = db.StringProperty()

class GroupsDB(db.Model):
	group_name = db.StringProperty(required=True)

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
		msg = dba.new_user_registry('lili', '1111', False)
		self.response.out.write("<p>" + msg[1] + "</p>")
		msg = dba.add_to_group('lady gaga', 'shakira')
		self.response.out.write("<p>" + msg[1] + "</p>")

	def post(self):
		dba=dataBaseClass()
		#user_request = ast.literal_eval(self.request.body)
		act = self.request.get('action')
		login = self.request.get('username')
		password = self.request.get('password')
		if user_request['action'] == 'new_user_registry':
			res,msg = dba.new_user_registry(login,password,False) 
			result = {'action' : act,'return value' : res,'msg' : msg}
			self.response.headers['Content-Type'] = 'application/JSON'
			self.response.out.write(json.dumps(result))

class dataBaseClass:
	def new_user_registry(self,username,password,isAdmin):
		try:
			match = db.GqlQuery("SELECT * "
				"FROM UsersDB "
				"WHERE user_name =:x ",
				x=username)
			user=match.get()
			if user == None:
				UsersDB(user_name = username,user_password = password,is_admin = isAdmin).put()
				return 1,username+' was added successfully',''
			elif user.user_password == password:
				return 1,username+' Welcome Back!',''
			else:
				return 0,'Wrong Password',''
		except datastore_errors,e:
			return 0,'adding failed : '+e,''

	def create_new_group(self, adminName, groupName):
		try:
			match = db.GqlQuery("SELECT * " "FROM GroupsDB " "WHERE group_name =:x ",x=groupName)
			group = match.get()
			if group == None:
				GroupsDB(group_name = groupName).put()
				try:
					match = db.GqlQuery("SELECT * " "FROM UsersDB " "WHERE user_name =:x ",x=adminName)
					admin = match.get()
					if admin.user_groupID == None:
						admin.user_groupID = groupName
						admin.put()
						return 1,groupName+" roomate group was created successfully",''
				except datastore_errors,e:
					return 0,'adding failed : '+e,''
				
			else:
				return 0, "sorry, this group name is taken",''
		except datastore_errors,e:
			return 0,'adding failed : '+e,''

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