from setuptools import setup, find_packages

with open("README.md", "r") as f:
    long_description = f.read()

setup(
    name='spl_types',
    version='1.0',
    url='https://github.com/ScaleRunner/compiler_construction',
    author='Dennis Verheijden',
    author_email='dennis-verheijden@live.nl',
    packages=find_packages(),
    description='This package contains all datatypes that are used in SPL',
    long_description=long_description,
    long_description_content_type="text/markdown",
    classifiers=(
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent"
    )
)
