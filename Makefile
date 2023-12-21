GOPROJECT := certmgr
GOCMDS := $(GOPROJECT)
GOMODULE := github.com/hdecarne-github/$(GOPROJECT)
GOMODULE_VERSION := $(shell cat version.txt)

GO := $(shell command -v go 2> /dev/null)
ifdef GO
GOOS ?= $(shell go env GOOS)
GOARCH ?= $(shell go env GOARCH)
LDFLAGS := $(LDFLAGS) -X $(GOMODULE)/internal/buildinfo.version=$(GOMODULE_VERSION) -X $(GOMODULE)/internal/buildinfo.timestamp=$(shell date +%Y%m%d%H%M%S)
ifneq (windows, $(GOOS))
GOCMDEXT :=
else
GOCMDEXT := .exe
endif
endif

NPM := $(shell command -v npm 2> /dev/null)
NPMOPTS ?= --no-progress --no-color --no-fund

WEB ?= 1

.DEFAULT_GOAL := help

.PHONY: help
help:
	@echo "Please use 'make <target>' where <target> is one of the following:"
	@echo "  make deps\tprepare dependencies"
	@echo "  make build\tbuild artifacts"
	@echo "  make dist\tcreate release package"
	@echo "  make check\ttest artifacts"
	@echo "  make clean\tdiscard build artifacts (not dependencies)"

.PHONY: init
init:
	@echo "Using build environment:"
ifndef GO
    $(error "ERROR: go command is not available")
endif
	@echo "  GO: $(GO) (GOOS:$(GOOS), GOARCH:$(GOARCH))"
ifndef NPM
    $(error "ERROR: npm command is not available")
endif
	@echo "  NPM: $(NPM)"

.PHONY: deps
deps: init
	@echo "Preparing dependencies..."
ifeq (1, $(WEB))
	cd internal/web && $(NPM) $(NPMOPTS) install
endif
	$(GO) mod download -x

.PHONY: build
build: deps
	@echo "Building artifacts..."
ifeq (1, $(WEB))
	cd internal/web && $(NPM) $(NPMOPTS) run build
endif
	mkdir -p "build/bin"
	$(foreach GOCMD, $(GOCMDS), $(GO) build -ldflags "-X $(GOMODULE)/internal/buildinfo.cmd=$(GOCMD) $(LDFLAGS)" -o "./build/bin/$(GOCMD)$(GOCMDEXT)" ./cmd/$(GOCMD);)

.PHONY: dist
dist: build
	@echo "Creating release package..."
	mkdir -p build/dist
	tar czvf build/dist/$(GOPROJECT)-$(GOOS)-$(GOARCH)-$(GOMODULE_VERSION).tar.gz -C build/bin .

.PHONY: check
check: deps
	@echo "Testing artifacts..."
ifeq (1, $(WEB))
	# cd internal/web && $(NPM) $(NPMOPTS) run test
endif
	$(GO) test -ldflags "$(LDFLAGS)" -v -coverpkg=./... -covermode=atomic -coverprofile=build/coverage.out ./...

.PHONY: clean
clean: init
	@echo "Cleaning build artifacts..."
	$(GO) clean ./...
	rm -rf "internal/web/build"
	rm -rf "build"

.PHONY: tidy
tidy: init
	go mod verify
	go mod tidy
