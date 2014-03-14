import sae
from main import app
application = sae.create_wsgi_app(app.wsgifunc()) 
#it's for SAE