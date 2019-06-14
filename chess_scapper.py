#import libraries
import requests	
import urllib.request
import time
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.common.exceptions import NoSuchElementException, \
	WebDriverException


url = 'http://wtharvey.com'

response = requests.get(url)

soup = BeautifulSoup(response.text, "html.parser")
i = 0
links_for_puzzles = soup.findAll('a')[7:705]
driver = webdriver.Chrome(executable_path='/usr/local/bin/chromedriver')

for link in links_for_puzzles:
	driver.get(url+"/"+link['href'])	
	print(url+ "/" + link['href'])
	time.sleep(1)
