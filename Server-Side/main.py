import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from firebase_admin import storage
import requests
import wget
import datetime

def download_file(url):
	local_filename = "test.3gp"
	# NOTE the stream=True parameter below
	with requests.get(url, stream=True) as r:
		r.raise_for_status()
		with open(local_filename, 'wb') as f:
			for chunk in r.iter_content(chunk_size=8192):
				f.write(chunk)
	return local_filename

cred = credentials.Certificate("mybaby-d3065-firebase-adminsdk-2119n-ba1b958d33.json")
firebase_admin.initialize_app(cred, {
	'databaseURL': "https://mybaby-d3065-default-rtdb.firebaseio.com/",
	'storageBucket': 'gs://mybaby-d3065.appspot.com',
	})
bucket = storage.bucket('mybaby-d3065.appspot.com')

# blob = bucket.blob("R8I55usQTEbv.3gp")
# download_url = blob.generate_signed_url(datetime.timedelta(seconds=300), method='GET')
# download_file(download_url)

def listener(event):
	print(event.data)

ref = db.reference("/requests").listen(listener)
