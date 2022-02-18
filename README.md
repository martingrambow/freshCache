# freshCache

freshCache triggers the rendering and caching of TYPO3 websites.

---

The website of my [research group](https://www.tu.berlin/mcc/) is TYPO3 based and renders a web page 
when it is called for the first time. Because this takes several seconds and generated pages are only cached 
for a few hours, our visitors often had to be patient.

FreshCache solves this problem by requesting and crawling the main page for further links to other subpages, 
where the search continues recursively.
---

**Arguments**:

    -h, --help          show this help message and exit

    -u URL, --url URL   base url which is in the beginning of every url ("mcc.tu-berlin.de" for the old website).

    -g GROUP,           reseach group ID ("/mcc/" for the new website, "mcc" for the old one).
    --group GROUP       

    -e EXCLUDE,         urls containing this substring will be ignored/excluded
    --exclude EXCLUDE   
    
    -w, --www           add www in front (for the old website, this flag must be turned on)


for the new website: ``-e rss -e cHash``

for the old website: ``-e rss -e /minhilfe/ -e /maxhilfe/ -e .css -e /sitemap/ -e /servicemenue/ -e isMobile= -e #``