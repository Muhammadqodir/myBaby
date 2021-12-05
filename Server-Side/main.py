import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from firebase_admin import storage
import requests
import datetime
import json
import pyperclip

cred = credentials.Certificate("mybaby-d3065-firebase-adminsdk-2119n-ba1b958d33.json")
firebase_admin.initialize_app(cred, {
	'databaseURL': "https://mybaby-d3065-default-rtdb.firebaseio.com/",
	'storageBucket': 'gs://mybaby-d3065.appspot.com',
	})
bucket = storage.bucket('mybaby-d3065.appspot.com')


def download_file(fileName):
	blob = bucket.blob(fileName)
	download_url = blob.generate_signed_url(datetime.timedelta(seconds=300), method='GET')
	pyperclip.copy(download_url)
	local_filename = "./downloads/"+fileName
	# NOTE the stream=True parameter below
	with requests.get(download_url, stream=True) as r:
		r.raise_for_status()
		with open(local_filename, 'wb') as f:
			for chunk in r.iter_content(chunk_size=8192):
				f.write(chunk)
	return local_filename

def listener(event):
	print(event.data)
	try:
		action = event.data["action"]
		if action == "recognize":
			fileName = event.data["fileName"]
			download_file(fileName)
			print("File was downloaded successfully!")
	except Exception as e:
		print(e)
		print("ok")

ref = db.reference("/requests").listen(listener)
