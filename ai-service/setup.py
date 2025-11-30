"""
Setup configuration for AI Service
"""

from setuptools import setup, find_packages

with open("README.md", "r", encoding="utf-8") as fh:
    long_description = fh.read()

with open("requirements.txt", "r", encoding="utf-8") as fh:
    requirements = [line.strip() for line in fh if line.strip() and not line.startswith("#")]

setup(
    name="ai-service",
    version="1.0.0",
    author="AI Service Team",
    author_email="team@example.com",
    description="Python-based AI and NLP microservice",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/example/ai-service",
    packages=find_packages(),
    classifiers=[
        "Development Status :: 4 - Beta",
        "Intended Audience :: Developers",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.11",
        "Programming Language :: Python :: 3.12",
        "Topic :: Scientific/Engineering :: Artificial Intelligence",
        "Topic :: Software Development :: Libraries :: Python Modules",
    ],
    python_requires=">=3.11",
    install_requires=requirements,
    extras_require={
        "dev": [
            "pytest>=7.4.3",
            "pytest-asyncio>=0.21.1",
            "black>=23.11.0",
            "isort>=5.12.0",
            "mypy>=1.7.1",
        ],
    },
    entry_points={
        "console_scripts": [
            "ai-service=main:run",
        ],
    },
    include_package_data=True,
    zip_safe=False,
)